from django.shortcuts import render

# Create your views here.
from django.http import HttpResponse
from .predict import predict_image

def home(request):
    result = None

    if request.method == "POST":
        image = request.FILES["image"]
        result = predict_image(image)

    return render(request, "myapp/home.html", {"result": result})