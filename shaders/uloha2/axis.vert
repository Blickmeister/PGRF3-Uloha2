#version 120
in vec3 inPositionAxis;
uniform mat4 model;
uniform mat4 view;
uniform mat4 proj;

void main() {
    gl_Position = proj * view * vec4(inPositionAxis, 1.0);
}
