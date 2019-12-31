#version 440

layout (triangles, equal_spacing, ccw) in;

out vec3 teNormal;
const float pi = 3.14159265359;

uniform mat4 model;
uniform mat4 view;
uniform mat4 proj;

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

    //gl_Position = proj * view * model * vec4(gl_TessCoord.x, gl_TessCoord.y, weier(gl_TessCoord.xy),1.0);

    gl_Position = gl_TessCoord.x * gl_in[0].gl_Position +
    gl_TessCoord.y * gl_in[1].gl_Position +
    gl_TessCoord.z * gl_in[2].gl_Position;

    /*vec4 v1 = vec4(gl_in[0].gl_Position.xy, weier(gl_in[0].gl_Position.xy),1.0);
    vec4 v2 = vec4(gl_in[1].gl_Position.xy, weier(gl_in[1].gl_Position.xy),1.0);
    vec4 v3 = vec4(gl_in[2].gl_Position.xy, weier(gl_in[2].gl_Position.xy),1.0);
    gl_Position = gl_TessCoord.x * v1 +
    gl_TessCoord.y * v2 +
    gl_TessCoord.z * v3;*/

    /*vec4 pos = vec4(gl_TessCoord.x * gl_in[0].gl_Position.x,
    gl_TessCoord.y * gl_in[1].gl_Position.y,
    gl_TessCoord.z * gl_in[2].gl_Position.z,1.0);*/

    //gl_Position = proj * view * model * pos;

}
