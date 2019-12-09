#version 440
layout(location = 1) in vec2 inPosition; // vstup z vertex bufferu
uniform mat4 model;
uniform mat4 view;
uniform mat4 proj;

// výpočet koule
vec3 getSphere(vec2 vec) {
    float az = vec.x * 3.14 *2;
    float ze = vec.y * 3.14;
    float r = 1;

    float x = r*cos(az)*cos(ze);
    float y = r*sin(az)*cos(ze);
    float z = r*sin(ze);

    return vec3(x, y, z);
}

// získání z hodnoty
float getFValue(vec2 vec){
    return -(vec.x*vec.x*5+vec.y*vec.y*5);
}


void main() {
    vec2 position = inPosition - 0.5;
    vec4 pos =  vec4( position, 0.0, 1.0 );
    gl_Position = pos;
}
