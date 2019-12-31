#version 430 core

//layout (vertices = 3) out;

layout (vertices = 3) out;
const float pi = 3.14159265359;
uniform float time;
uniform mat4 model;
uniform mat4 view;
uniform mat4 proj;
uniform int objType;

void main() {

    //if(objType == 0) {
        if (gl_InvocationID == 0){
            gl_TessLevelInner[0] = time;
            gl_TessLevelOuter[0] = 3;
            gl_TessLevelOuter[1] = 5;
            gl_TessLevelOuter[2] = 4;
        }
   // }
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

    gl_out[gl_InvocationID].gl_Position = gl_in[gl_InvocationID].gl_Position;

}
