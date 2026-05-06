#define TF_LITE_STATIC_MEMORY
#include <tflm_esp32.h>
#include <eloquent_tinyml.h>
#include "esp_task_wdt.h"
#include "test_img.h"
#include "sports.h"

#define TF_NUM_INPUTS 224*224*3
#define TF_NUM_OUTPUTS 100 // Your number of classes
#define ARENA_SIZE (700*1024)
#define NUM_OPS 60

Eloquent::TF::Sequential<NUM_OPS, ARENA_SIZE>* tf;

void setup() {
    Serial.begin(115200);
    delay(3000);
    
    Serial.println("Initializing...");
    if(psramInit()){
        Serial.printf("PSRAM Total: %d bytes\n", ESP.getPsramSize());
        Serial.printf("PSRAM Free: %d bytes\n", ESP.getFreePsram());
    } else {
        Serial.println("PSRAM hardware not responding!");
    }
    Serial.println("PSRAM Initialized!");
    
    tf = (Eloquent::TF::Sequential<NUM_OPS, ARENA_SIZE>*) ps_malloc(sizeof(Eloquent::TF::Sequential<NUM_OPS, ARENA_SIZE>));

    if (tf != NULL) {
        // 2. IMPORTANT: Call the constructor at that memory address (Placement New)
        new (tf) Eloquent::TF::Sequential<NUM_OPS, ARENA_SIZE>(); 
        Serial.println("Model object fully initialized in PSRAM!");
    } else {
        Serial.println("PSRAM Allocation Failed!");
        while(1);
    }
    
    Serial.println("Model allocated!");
    tf->setNumInputs(TF_NUM_INPUTS);
    tf->setNumOutputs(TF_NUM_OUTPUTS);
    Serial.println("Model setup!");

    esp_task_wdt_delete(NULL);
    // 3. Pass the Arena size here; the library will allocate it dynamically
    if (!tf->begin(sports_model_tflite).isOk()) {
        Serial.println("Model Begin Failed!");
        Serial.println(tf->exception.toString());
    }
    esp_task_wdt_add(NULL);
    Serial.println("Model started successfully!");
}

void loop() {
    // 2. Cast the input to (void*) to satisfy the compiler's 'const' check
    /*if (!tf->predict((int8_t*)test_image_data).isOk()) {
        Serial.println("Inference Failed!");
        Serial.println(tf->exception.toString());
    } else {
        Serial.print("Winner Class: ");
        Serial.println(tf->classification);
    }*/

    delay(5000);
}