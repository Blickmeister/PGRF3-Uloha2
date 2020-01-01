#version 440

layout (triangles, equal_spacing, ccw) in;

out vec3 teNormal;
const float pi = 3.14159265359;

uniform mat4 model;
uniform mat4 view;
uniform mat4 proj;
// pro výpočet barvy ve FS
patch in vec4 patch_color;
out vec4 tes_color;

void main() {
    gl_Position = gl_TessCoord.x * gl_in[0].gl_Position +
    gl_TessCoord.y * gl_in[1].gl_Position +
    gl_TessCoord.z * gl_in[2].gl_Position;

    tes_color = patch_color;
}
