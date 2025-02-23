#!/usr/bin/env python3
import pika
import ssl
import json
import pymysql
from pymongo import MongoClient
from datetime import datetime
from bson import ObjectId
from bson.json_util import dumps  # ObjectId 직렬화를 위한 모듈

# MySQL 설정
MYSQL_CONFIG = {
    "host": "jkah.shop",  
    "user": "hdg0056",      
    "password": "ehdrbs159",  
    "database": "aiot"       
}

#MongoDB 설정
MONGO_CLIENT = MongoClient("mongodb://hdg0056:ehdrbs159@jkah.shop:27017/?authSource=IoT")
MONGO_DB = MONGO_CLIENT["IoT"]
MONGO_COLLECTION = MONGO_DB["device_data"]

#RabbitMQ 설정
RABBITMQ_CONFIG = {
    "host": "goqual.io",
    "port": 55001,
    "virtual_host": "/",
    "username": "d4229889b00140b19469272ff9f5b6b8",
    "password": "480c2b85dc2447948ffe705bb564098c",
    "queue": "d4229889b00140b19469272ff9f5b6b8"
}

def convert_unix_to_readable(unix_time):
    #유닉스 타임스탬프(밀리초)를 변환
    return datetime.fromtimestamp(unix_time / 1000).strftime('%Y-%m-%d %H:%M:%S')

def get_plug_data():
    #MySQL에서 plug_id와 관련 정보를 가져오기
    with pymysql.connect(**MYSQL_CONFIG) as connection:
        with connection.cursor(pymysql.cursors.DictCursor) as cursor:
            cursor.execute("SELECT plug_id, owner, plug_name, actual_device FROM plug")
            return cursor.fetchall()

def save_to_mongo(data):
    #MongoDB에 데이터 저장
    result = MONGO_COLLECTION.insert_one(data)
    data["_id"] = str(result.inserted_id)  # ObjectId → 문자열 변환
    print("[MongoDB 저장 완료]")
    print(json.dumps(data, indent=4, ensure_ascii=False))  # JSON 형식으로 출력

def process_message(message, plug_data):
    #RabbitMQ 메시지 처리 후 MongoDB 저장
    try:
        device_data = message.get("deviceDataReport", {})
        dev_id = device_data.get("devId")

        #MySQL에서 plug_id 매칭
        matching_plug = next((plug for plug in plug_data if plug["plug_id"] == dev_id), None)

        if matching_plug:
            #유닉스 타임스탬프 변환
            if device_data.get("status"):
                for status in device_data["status"]:
                    if "t" in status:
                        status["t_readable"] = convert_unix_to_readable(status["t"])

            #MongoDB 저장 데이터 구성
            mongo_data = {
                "plug_id": dev_id,
                "plug_name": matching_plug["plug_name"],
                "timestamp_readable": convert_unix_to_readable(device_data["status"][0]["t"]),
                "status": device_data["status"]
            }

            save_to_mongo(mongo_data)  # MongoDB에 데이터 저장
        else:
            print(f"MySQL에 등록되지 않은 plug_id: {dev_id}")

    except json.JSONDecodeError:
        print("[오류] JSON 파싱 실패")
    except Exception as e:
        print(f"[오류] 데이터 처리 중 예외 발생: {e}")
 
 #RabbitMQ 메시지 처리하는 메인 함수
def main():

    plug_data = get_plug_data()
    print("[MySQL에서 가져온 플러그 데이터]:")
    print(json.dumps(plug_data, indent=4, ensure_ascii=False))

    # RabbitMQ 연결 설정
    context = ssl.SSLContext(ssl.PROTOCOL_TLS_CLIENT)
    context.check_hostname = False
    context.verify_mode = ssl.CERT_NONE

    parameters = pika.ConnectionParameters(
        host=RABBITMQ_CONFIG["host"],
        port=RABBITMQ_CONFIG["port"],
        virtual_host=RABBITMQ_CONFIG["virtual_host"],
        credentials=pika.PlainCredentials(
            RABBITMQ_CONFIG["username"],
            RABBITMQ_CONFIG["password"]
        ),
        ssl_options=pika.SSLOptions(context),
        heartbeat=100
    )

    connection = pika.BlockingConnection(parameters)
    channel = connection.channel()

    def callback(ch, method, properties, body):
        try:
            message = json.loads(body)
            print(f"\n[x] Received 메시지:")
            print(json.dumps(message, indent=4, ensure_ascii=False))  # 보기 좋은 JSON 출력
            process_message(message, plug_data)
        except json.JSONDecodeError:
            print("[오류] 메시지 디코딩 실패")

    channel.basic_consume(queue=RABBITMQ_CONFIG["queue"], on_message_callback=callback, auto_ack=True)

    print("[*] RabbitMQ 메시지를 기다리는 중. 종료하려면 CTRL+C를 누르세요.")
    channel.start_consuming()

if __name__ == '__main__':
    try:
        main()
    except KeyboardInterrupt:
        print("\n[*] 프로그램 종료")
