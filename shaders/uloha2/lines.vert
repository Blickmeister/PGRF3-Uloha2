#version 120
// vstupní buffery
in vec3 inPositionAxis;
in vec2 inPositionBol1N;
in vec2 inPositionBol2N;
in vec2 inPositionBol3N;
in vec2 inPositionBol4N;
in vec2 inPositionBol5N;
// mvp matice
uniform mat4 model;
uniform mat4 view;
uniform mat4 proj;
// prepinani modu a zobrazeni od uzivatele
uniform int objType;
uniform float zoom;
uniform float px;
uniform float py;

void main() {
    if (objType == 0) { // osy
        vec3 position = vec3(inPositionAxis.x+px+px, inPositionAxis.y, inPositionAxis.z+py+py);
        gl_Position = proj * view * vec4(position*zoom, 1.0);
    } else if (objType == 1) { // Bolzan N=1
        vec3 position = vec3(inPositionBol1N.x+px, 0, inPositionBol1N.y+py);
        gl_Position = proj * view * model * vec4(position*zoom, 1.0);
    } else if (objType == 2) { // Bolzan N=2
        vec3 position = vec3(inPositionBol2N.x+px, 0, inPositionBol2N.y+py);
        gl_Position = proj * view * model * vec4(position*zoom, 1.0);
    } else if (objType == 3) { // Bolzan N=3
        vec3 position = vec3(inPositionBol3N.x+px, 0, inPositionBol3N.y+py);
        gl_Position = proj * view * model * vec4(position*zoom, 1.0);
    } else if (objType == 4) { // Bolzan N=4
        vec3 position = vec3(inPositionBol4N.x+px, 0, inPositionBol4N.y+py);
        gl_Position = proj * view * model * vec4(position*zoom, 1.0);
    } else { // Bolzan N=5
        vec3 position = vec3(inPositionBol5N.x+px, 0, inPositionBol5N.y+py);
        gl_Position = proj * view * model * vec4(position*zoom, 1.0);
    }
}
