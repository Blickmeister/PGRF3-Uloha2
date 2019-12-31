#version 440
layout(location = 1) in vec2 inPosition;// vstup z vertex bufferu
in vec3 inPositionBol;
uniform mat4 model;
uniform mat4 view;
uniform mat4 proj;
uniform int objType;
uniform int view3D;
// parametry Weierstrassovy fce od uživatele
uniform float b;
uniform int N;

const float pi = 3.14159265359;

// Weierstrassova funkce
//počet iterací nastavuje uživatel (omezeno na 50) a parametr b se inkrementuje v animaci (omezeno na 5)
vec3 weier(vec2 vec){
    const float a = 0.8; // interval 0-1
    float x;
    float y;
    if(view3D == 1) {
        x = vec.x;
        y = vec.y;
    } else {
        x = vec.x;
        y = 0;
    }
    float z;
    int n = 0;
    z = pow(a, n)*cos(pi*pow(b, n)*x);
    for (int n = 1; n <= N; n++) {
     z = z + pow(a, n)*cos(pi*pow(b, n)*x);
    }
    return vec3(x,y,z);
}

vec3 bolzano(vec2 vec) {
    float x;
    float y;
    if(view3D == 1) {
        x = vec.x;
        y = vec.y;
    } else {
        x = vec.x;
        y = 0;
    }
    float z;
    float x0 = 0;
    float z0 = 0;
    float xn = 1;
    float zn = 1;
    for(int n = 1; n <=N; n++) {

    }
    return vec3(x,y,z);
}

void main() {
   // if(objType == 0) {
        vec2 position = inPosition - 2.5;
        vec4 pos;

        gl_Position = proj * view * model * vec4(weier(position), 1.0);
   // } else {
    //    vec3 position = inPositionBol;
    //    vec4 pos;
   //     gl_Position = proj * view * model * vec4(position, 1.0);
   // }
}
