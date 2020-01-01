#version 120
in vec4 tes_color;
out vec4 outColor;// output from the fragment shader
uniform int view3D;

void main() {
    if (view3D == 1) { // vypocet barvy pro 3D zobrazeni dle z
        outColor = vec4(0.8, 0.8, 0.8, 1) * vec4(tes_color.z * 2, 0.8, 0.8, 1.0);
    } else {
        outColor = vec4(1.0, 0.0, 0.0, 1.0);
    }
}
