import os
import json
import numpy as np
import tensorflow as tf
from PIL import Image

from .ml_loader import get_model


# ==========================================
# LOAD REAL CLASS NAMES
# ==========================================

BASE_DIR = os.path.dirname(os.path.abspath(__file__))

CLASSES_PATH = os.path.join(BASE_DIR, "classes.json")

with open(CLASSES_PATH, "r") as f:
    CLASS_NAMES = json.load(f)


# ==========================================
# PREDICT IMAGE
# ==========================================

def predict_image(image_path):

    model = get_model()

    # Open image
    img = Image.open(image_path).convert("RGB")

    # Resize
    img = img.resize((224, 224))

    # Convert to numpy
    img = np.array(img).astype("float32")

    # IMPORTANT:
    # MobileNetV3 already preprocess inside model
    # because include_preprocessing=True

    # Add batch dimension
    img = np.expand_dims(img, axis=0)

    # Prediction
    predictions = model.predict(img, verbose=0)

    # Convert logits -> probabilities
    probabilities = tf.nn.softmax(predictions[0]).numpy()

    # Top 3
    top_indices = np.argsort(probabilities)[-3:][::-1]

    results = []

    for idx in top_indices:

        results.append({
            "class": CLASS_NAMES[idx],
            "confidence": round(float(probabilities[idx] * 100), 2)
        })

    return results