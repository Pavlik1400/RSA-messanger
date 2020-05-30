import os
import datetime
import sqlite3


def create_db(room_num):
    """
    Creates database of room_num
    """
    conn = sqlite3.connect(f"/home/yexp/mysite/dbs/{str(room_num)}.db")
    cursor = conn.cursor()

    cursor.execute("""CREATE TABLE room
                      (author text, message text, time text)
                   """)

    conn.commit()


def get_room_messages(room_num):
    """
    Gets messages from room_num
    """
    if str(room_num) + ".db" not in os.listdir("/home/yexp/mysite/dbs"):
        create_db(room_num)
        return {}

    conn = sqlite3.connect(f"/home/yexp/mysite/dbs/{str(room_num)}.db")
    cursor = conn.cursor()

    cursor.execute("""SELECT * FROM room""")
    data = cursor.fetchall()

    if len(data) > 20:
        cursor.execute(f"""DELETE FROM room WHERE message = '{data[0][1]}'""")
        conn.commit()

    result = dict()
    result["response"] = []

    for message in data:
        result["response"].append({"author": message[0], "message": message[1], "time": message[2]})

    return result


def post_room_messages(room_num, author, message):
    """
    Post message in room_num
    """
    if str(room_num) + ".db" not in os.listdir("/home/yexp/mysite/dbs"):
        create_db(room_num)

    conn = sqlite3.connect(f"/home/yexp/mysite/dbs/{str(room_num)}.db")
    cursor = conn.cursor()

    cursor.execute(f"""INSERT INTO room
                      VALUES ('{author}', '{message}', '{datetime.datetime.today().isoformat()}')"""
                   )

    conn.commit()

def remove_room(room_num):
    """
    Removes room
    """
    if str(room_num) + ".db" not in os.listdir("/home/yexp/mysite/dbs"):
        return {}

    os.remove(f'/home/yexp/mysite/dbs/{str(room_num)}.db')
    return {"status": 200}
