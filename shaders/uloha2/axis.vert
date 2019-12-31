#version 120
in vec3 inPositionAxis;
in vec2 inPositionBol1N;
in vec2 inPositionBol2N;
in vec2 inPositionBol3N;
in vec2 inPositionBol4N;
in vec2 inPositionBol5N;
uniform mat4 model;
uniform mat4 view;
uniform mat4 proj;
uniform int objType;

void main() {
    if(objType == 0) {
        gl_Position = proj * view * vec4(inPositionAxis, 1.0);
    } else if(objType == 1) {
        vec2 position = inPositionBol1N;
        gl_Position = proj * view * model * vec4(position.x,0,position.y, 1.0);
    } else if(objType == 2) {
        vec2 position = inPositionBol2N;
        gl_Position = proj * view * model * vec4(position.x,0,position.y, 1.0);
    } else if(objType == 3) {
        vec2 position = inPositionBol3N;
        gl_Position = proj * view * model * vec4(position.x,0,position.y, 1.0);
    } else if(objType == 4) {
        vec2 position = inPositionBol4N;
        gl_Position = proj * view * model * vec4(position.x,0,position.y, 1.0);
    } else {
        vec2 position = inPositionBol5N;
        gl_Position = proj * view * model * vec4(position.x,0,position.y, 1.0);
    }
}
