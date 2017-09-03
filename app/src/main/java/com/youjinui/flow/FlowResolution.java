package com.youjinui.flow;

/**
 * 플로우 해상도 변수 및 해상도 조정 클래스
 */

public class FlowResolution {

    public static final int ZoomMax = 5; //확대 최대 횟수
    public static final int ZoomMin = 5; //축소 최대 횟수
    public static int CurrentMagnification = 0; //0 = 기본값, -ZoomMax ~ +ZoomMin

    public static class PaintResolution{
        static float strokeWidthP = 3;
        static float textSizeP = 35;
    }

    public static class StartResolution{
        static float roundS = 50;
        static float heightS = 65;
        static float marginS = 150;
    }

    public static class EndResolution{ //모서리가 둥근 사각형
        static float roundE = 50;
        static float heightE = 65;
        static float marginE = 150;
    }

    public static class Ready{ //육각형 경사
        static float RdipX = 48;
        static float RdipY = 32.5f;
        static float marginR = 100;
    }

    public static class Cheory{
        static float marginC = 100;
        static float heightC = 65;
    }

    public static class Input{
        static float marginI = 100;
        static float heightI = 65;
        static float GradientI = 50;
    }

    public static class Output{
        static float marginO = 150;
        static float heightO = 65;
        static float CurveI = 25;
    }

    public static class Condition{
        static float marginCo = 150;
        static float heightMagnificationCo = 3.5f;
    }

    public static class Repeat{
        static float titleGap = 65;
        static float basicWidth = 500;
        static float basicHeight = 500;
        static float marginR = 40;
    }

    public static class funcStart{
        static float roundfS = 50;
        static float heightfS = 65;
        static float marginfS = 100;
    }

    public static class funcEnd{
        static float roundfE = 50;
        static float heightfE = 65;
        static float marginfE = 100;
    }

    public static class funcUse{
        static float marginfU = 100;
        static float heightfU = 65;
    }

    public static class Liner{
        static float arrow = 10;
    }
}
