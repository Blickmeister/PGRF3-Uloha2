#version 440
// vstupy z vertex bufferu
layout(location = 1) in vec2 inPosition;
in vec3 inPositionBol;
// mvp matice
uniform mat4 model;
uniform mat4 view;
uniform mat4 proj;
// prepinani modu a zobrazeni od uzivatele
uniform int objType;
uniform int view3D;
uniform float zoom;
uniform float px;
uniform float py;
// parametry Weierstrassovy fce od uživatele
uniform float b;
uniform int N;

out vec4 objPos;// output vertex pro výpočet obarvení

const float pi = 3.14159265359;

// Weierstrassova funkce
//počet iterací nastavuje uživatel (omezeno na 50) a parametr b se inkrementuje v animaci (omezeno na 5)
vec3 weier(vec2 vec){
    const float a = 0.8;// interval 0-1
    float x;
    float y;
    if (view3D == 1) {
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
    return vec3(x+px, y, z+py);
}

void main() {
    vec2 position = inPosition - 2.5;
    vec4 pos;
    objPos = vec4(weier(position), 1.0);

    gl_Position = proj * view * model * vec4(weier(position)*zoom, 1.0);
}
