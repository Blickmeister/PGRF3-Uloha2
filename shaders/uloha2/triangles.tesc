#version 430 core

layout (vertices = 3) out;
const float pi = 3.14159265359;
uniform float time;
uniform mat4 model;
uniform mat4 view;
uniform mat4 proj;
uniform int objType;
// pro výpočet barvy ve FS
in  vec4 objPos [];
patch out vec4 patch_color;
void main() {

    if (gl_InvocationID == 0){
        gl_TessLevelInner[0] = time;
        gl_TessLevelOuter[0] = 3;
        gl_TessLevelOuter[1] = 5;
        gl_TessLevelOuter[2] = 4;
    }

    gl_out[gl_InvocationID].gl_Position = gl_in[gl_InvocationID].gl_Position;

    patch_color = objPos [gl_InvocationID];

}
