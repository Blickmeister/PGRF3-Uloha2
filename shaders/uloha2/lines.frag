#version 120
uniform int objType;

void main() {
    if (objType == 0) {
        gl_FragColor = vec4(0.0, 1.0, 0.0, 1.0);
    } else if (objType == 1) {
        gl_FragColor = vec4(1.0, 1.0, 1.0, 1.0);
    } else if (objType == 2) {
        gl_FragColor = vec4(1.0, 1.0, 0.0, 1.0);
    } else if (objType == 3) {
        gl_FragColor = vec4(0.0, 0.0, 1.0, 1.0);
    } else if (objType == 4) {
        gl_FragColor = vec4(1.0, 0.0, 1.0, 1.0);
    } else {
        gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);
    }
}
