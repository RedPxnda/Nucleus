#version 150

#moj_import <fog.glsl>

in vec3 Position;
in vec4 Color;
in vec3 Normal;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform float LineWidth;
uniform vec2 ScreenSize;
uniform int FogShape;

out float vertexDistance;
out vec4 vertexColor;

const float VIEW_SHRINK = 1.0 - (1.0 / 256.0);
const mat4 VIEW_SCALE = mat4(
VIEW_SHRINK, 0.0, 0.0, 0.0,
0.0, VIEW_SHRINK, 0.0, 0.0,
0.0, 0.0, VIEW_SHRINK, 0.0,
0.0, 0.0, 0.0, 1.0
);

void main() {
    vec4 linePosStart = ProjMat * VIEW_SCALE * ModelViewMat * vec4(Position, 1.0);
    vec4 linePosEnd = ProjMat * VIEW_SCALE * ModelViewMat * vec4(Position + Normal, 1.0);

    vec3 ndc1 = linePosStart.xyz / linePosStart.w;
    vec3 ndc2 = linePosEnd.xyz / linePosEnd.w;

    vec2 lineScreenDirection = normalize((ndc2.xy - ndc1.xy) * ScreenSize);
    vec2 lineOffset = vec2(-lineScreenDirection.y, lineScreenDirection.x) * LineWidth / ScreenSize;

    if (lineOffset.x < 0.0) {
        lineOffset *= -1.0;
    }

    float t = gl_VertexID == 0 ? 0.0 : 1.0; // Interpolation factor between start and end points

    // Calculate the vertex position by interpolating between start and end points
    vec3 vertexPos = mix(ndc1, ndc2, t);

    // Calculate the width of the triangle at the current vertex
    float width = mix(LineWidth, 0.0, t);

    // Calculate the offset for the isosceles triangle
    float halfWidth = LineWidth * 0.5;
    float triangleOffset = halfWidth - width;

    // Offset the vertex position by the triangleOffset to create the isosceles triangle shape
    gl_Position = vec4((vertexPos + vec3(lineOffset * triangleOffset, 0.0)) * linePosStart.w, linePosStart.w);

    vertexDistance = fog_distance(ModelViewMat, Position, FogShape);
    vertexColor = Color;
}