from flask import Flask, request, jsonify, abort, make_response
import app_database


app = Flask(__name__)


@app.errorhandler(404)
def not_found(error):
    """
    Handles 404 error
    """
    return make_response(jsonify({'error': 'Not found'}), 404)


@app.errorhandler(400)
def not_found(error):
    """
    Handles 400 error
    """
    return make_response(jsonify({'error': 'Empty request'}), 400)


@app.route('/api/v1/get_rooms/<string:room_num>', methods=['GET'])
def get_room_messages(room_num):
    """
    Gets data from entered room
    For example: host/api/v1/get_rooms/2 - gets info fom room 2
    """
    data = app_database.get_room_messages(room_num)

    if len(data) == 0:
        abort(404)

    return jsonify(data)


@app.route('/api/v1/post_rooms/<string:room_num>', methods=['POST'])
def post_room_messages(room_num):
    """
    Post message to the room
    Send json in format {'author': Name of author, 'message': message}
    """

    if not request.json or not 'author' in request.json:
        abort(400)

    message = request.json['message']
    author = request.json['author']

    app_database.post_room_messages(room_num, author, message)
    return jsonify({'status': 200})


@app.route('/api/v1/del_rooms/<string:room_num>', methods=['GET'])
def delete_room(room_num):
    """
    Deletes room
    """
    return jsonify(app_database.remove_room(room_num))
