#version 430 core

//layout (vertices = 3) out;

layout (vertices = 3) out;

uniform float time;

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

    //vec4 pos = vec4(getSphere(gl_in[gl_InvocationID].gl_Position.xy),1.0);
    vec4 pos = vec4(gl_in[gl_InvocationID].gl_Position.xy, getFValue(gl_in[gl_InvocationID].gl_Position.xy), 1.0);


    gl_out[gl_InvocationID].gl_Position = pos;

    //gl_out[gl_InvocationID].gl_Position = gl_in[gl_InvocationID].gl_Position;

}
