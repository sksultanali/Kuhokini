package com.kuhokini.Helpers;

import android.os.Handler;
import android.widget.TextView;

import java.util.List;

public class TypeAnimation {

    private Handler handler;
    List<String> textList;
    TextView textView;
    int listIndex = 0;
    int charIndex = 0;
    long typingDelay = 100;
    long deletingDelay = 50;
    long showDelay = 2000;
    boolean isDeleting = false;

    public TypeAnimation(Handler handler, List<String> textList, TextView textView) {
        this.handler = handler;
        this.textList = textList;
        this.textView = textView;
    }

    public void startTypingAnimation() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String currentText = textList.get(listIndex);
                if (!isDeleting) {
                    if (charIndex < currentText.length()) {
                        textView.setText(currentText.substring(0, charIndex + 1));
                        charIndex++;
                        handler.postDelayed(this, typingDelay);
                    } else {
                        handler.postDelayed(() -> isDeleting = true, showDelay);
                        handler.postDelayed(this, showDelay);
                    }
                } else {
                    if (charIndex > 0) {
                        textView.setText(currentText.substring(0, charIndex - 1));
                        charIndex--;
                        handler.postDelayed(this, deletingDelay);
                    } else {
                        isDeleting = false;
                        listIndex = (listIndex + 1) % textList.size();
                        handler.postDelayed(this, 0);
                    }
                }
            }
        }, typingDelay);
    }
}
