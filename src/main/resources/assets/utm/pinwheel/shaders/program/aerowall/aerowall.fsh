uniform sampler2D Sampler0;
uniform float GameTime;

in vec2 texCoord;
in float vDistance;

out vec4 fragColor;


void main() {
    vec4 tex = texture(Sampler0, texCoord);
    float fadeFactor = 1.0 - smoothstep(2.0, 7.0, vDistance);
    float alphaMultiplier = mix(0.1, 1.0, fadeFactor);
    float boostFactor = 1.0 - smoothstep(1.0, 3.0, vDistance);
    float alpha = 0;
    if (tex.a > 0) {
        alpha = 1;
    }

    vec3 finalColor = tex.rgb * (1+(boostFactor/4));
    float finalAlpha = alpha * alphaMultiplier * (1 - boostFactor);

    fragColor = vec4(finalColor, finalAlpha);
}