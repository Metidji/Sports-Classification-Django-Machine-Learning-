import tensorflow as tf

model = None

def get_model():
    global model
    if model is None:
        model = tf.keras.models.load_model("myapp/ml/sports.keras")
    return model