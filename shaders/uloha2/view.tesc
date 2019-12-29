#version 430 core

//layout (vertices = 3) out;

layout (vertices = 3) out;
const float pi = 3.14159265359;
uniform float time;
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

// Weierstrassova funkce
float weier(vec2 vec){
    float x = vec.x;
    float y = vec.y;
    float a = 0.5;
    float b = 5;
    float z;
    int n = 1;
    //z = pow(a, n)*cos(pi*pow(b, n)*(vec.x+vec.y));
    z = pow(a, n)*cos(pi*pow(b, n)*x) + pow(a, n+1)*cos(pi*pow(b,n+1)*x);
   // for (n = 2; n <= 10; n++) {
        //z = z + pow(a, n)*cos(pi*pow(b, n)*(vec.x+vec.y));
       // z = z + pow(a, n)*cos(pi*pow(b, n)*x);
    //}
    return z;
}

void main() {

    if (gl_InvocationID == 0){
        gl_TessLevelInner[0] = time;
        gl_TessLevelOuter[0] = 3.0;
        gl_TessLevelOuter[1] = 4.0;
        gl_TessLevelOuter[2] = 2.0;
    }
    /*if (gl_InvocationID == 1){
        gl_TessLevelInner[0] = 2.0;
        gl_TessLevelOuter[0] = 3.0;
        gl_TessLevelOuter[1] = 4.0;
        gl_TessLevelOuter[2] = 2.0;
    }
    if (gl_InvocationID == 2){
        gl_TessLevelInner[0] = 2.0;
        gl_TessLevelOuter[0] = 3.0;
        gl_TessLevelOuter[1] = 4.0;
        gl_TessLevelOuter[2] = 2.0;
    }*/

    //if (gl_InvocationID == 0)
    //     gl_out[gl_InvocationID].gl_Position = vec4(0,0,0,1);
    //else

    //vec4 pos =

    vec4 pos = vec4(gl_in[gl_InvocationID].gl_Position.xy, weier(gl_in[gl_InvocationID].gl_Position.xy), 1.0);
    gl_out[gl_InvocationID].gl_Position = proj * view * model * pos;

    //gl_out[gl_InvocationID].gl_Position = gl_in[gl_InvocationID].gl_Position;

}
