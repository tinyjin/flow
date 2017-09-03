package com.youjinui.flow;

import static com.youjinui.flow.FlowDrawer.getTextSize;
import static com.youjinui.flow.FlowResolution.*;
import static com.youjinui.flow.Tag.findTagByUUID;

/**
 * 플로우 크기 조정기 : Repeat, ConditionRepeat, Function, <FlowScreen>
 */

public class FlowAutoSizer {

    public static void RepeatAutoSizing(FlowTag.Repeat repeat){
        if(repeat.RepeatHeader.equals("")){
            repeat.SetWidth(Repeat.basicWidth);
            repeat.SetHeight(Repeat.basicHeight);
            return;
        }

        Tag header = findTagByUUID(repeat.RepeatHeader);
        String text = repeat.GetText();

        float textSize = getTextSize(text);
        float codeWidth = GetContainCodeWidth(header);

        if(textSize > codeWidth){
            repeat.SetWidth(textSize + Repeat.marginR * 2);
        }
        else{
            repeat.SetWidth(codeWidth + Repeat.marginR * 2);
        }

        float codeHeight = GetContainCodeHeight(header) + Repeat.marginR * 2;
        repeat.SetHeight(codeHeight + Repeat.titleGap);
    }

    private static float GetContainCodeWidth(Tag header){
        Tag tag = header;
        float minLeft, maxRight;
        minLeft = header.GetLeft();
        maxRight = header.GetRight();

        while(!tag.GetNext().equals("")) {
            tag = findTagByUUID(tag.GetNext());
            float left, right, top, bottom;
            left = tag.GetLeft();
            right = tag.GetRight();

            //Left 검사
            if(minLeft > left) minLeft = left;
            //Right 검사
            if(maxRight < right) maxRight = right;

        }

        float width = maxRight - minLeft;

        return width;
    }

    private static float GetContainCodeHeight(Tag header){
        Tag tag = header;
        float minTop, maxBottom;

        minTop = header.GetTop();
        maxBottom = header.GetBottom();

        while(!tag.GetNext().equals("")) {
            tag = findTagByUUID(tag.GetNext());
            float top, bottom;
            top = tag.GetTop();
            bottom = tag.GetBottom();

            //Top 검사
            if(minTop > top) minTop = top;
            //Bottom 검사
            if(maxBottom < bottom) maxBottom = bottom;
        }

        float height = maxBottom - minTop;

        return height;
    }

}
