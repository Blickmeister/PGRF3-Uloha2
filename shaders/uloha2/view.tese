#version 440

layout (triangles, equal_spacing, ccw) in;

out vec3 teNormal;

void main() {

    gl_Position = gl_TessCoord.x * gl_in[0].gl_Position +
    gl_TessCoord.y * gl_in[1].gl_Position +
    gl_TessCoord.z * gl_in[2].gl_Position;

    /*vec4 pos = vec4(gl_TessCoord.x * gl_in[0].gl_Position.x,
    gl_TessCoord.y * gl_in[1].gl_Position.y,
    gl_TessCoord.z * gl_in[2].gl_Position.z,1.0);*/

    //gl_Position = proj * view * model * pos;

}
