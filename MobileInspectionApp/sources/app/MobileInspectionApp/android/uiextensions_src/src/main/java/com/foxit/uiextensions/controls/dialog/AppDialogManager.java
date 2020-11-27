/**
 * Copyright (C) 2003-2020, Foxit Software Inc..
 * All Rights Reserved.
 * <p>
 * http://www.foxitsoftware.com
 * <p>
 * The following code is copyrighted and is the proprietary of Foxit Software Inc.. It is not allowed to
 * distribute any parts of Foxit PDF SDK to third party or public without permission unless an agreement
 * is signed between Foxit Software Inc. and customers to explicitly grant customers permissions.
 * Review legal.txt for additional license and legal information.
 */
package com.foxit.uiextensions.controls.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.widget.PopupWindow;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.Stack;

public class AppDialogManager {
    class E {
        Object obj;
        CancelListener listener;

        E(Object obj, CancelListener listener) {
            this.obj = obj;
            this.listener = listener;
        }
    }

    public interface CancelListener {
        void cancel();
    }

    private final Stack<E> mStack = new Stack<E>();

    private static AppDialogManager mInstance = null;

    public static AppDialogManager getInstance() {
        if (mInstance == null) {
            mInstance = new AppDialogManager();
        }

        return mInstance;
    }

    protected AppDialogManager() {
    }

    public void showAllowManager(DialogFragment fragment, FragmentManager manager, String tag, CancelListener listener) {
        showInner(true, fragment, manager, tag, listener);
    }

    public void show(DialogFragment fragment, FragmentManager manager, String tag, CancelListener listener) {
        showInner(false, fragment, manager, tag, listener);
    }

    private void showInner(boolean allowManager, DialogFragment fragment, FragmentManager manager, String tag, CancelListener listener) {
        if (fragment == null) return;
        try {
            FragmentTransaction transaction = manager.beginTransaction();
            Fragment targetFragment = manager.findFragmentByTag(tag);
            if (targetFragment != null) {
                transaction.remove(targetFragment);
            }
            transaction.add(fragment, tag);
            transaction.commitAllowingStateLoss();
            boolean contains = false;
            int size = mStack.size();
            for (int i=0; i<size; i++){
                E e = mStack.get(i);
                if (e.obj == fragment){
                    contains = true;
                    break;
                }
            }
            if (allowManager && !contains) {
                mStack.push(new E(fragment, listener));
            }
        } catch (Exception e) {
            if (listener != null) {
                listener.cancel();
            }
            e.printStackTrace();
        }
    }

    public void dismiss(DialogFragment fragment) {
        remove(fragment);
        if (fragment.isDetached()) return;
        try {
            fragment.dismissAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void remove(DialogFragment fragment) {
        if (fragment == null) return;
        int size = mStack.size();
        for (int i=0; i<size; i++){
            E e = mStack.get(i);
            if (e.obj == fragment){
                mStack.remove(e);
                break;
            }
        }
    }

    public void showAllowManager(Dialog dialog, CancelListener listener) {
        showInner(true, dialog, listener);
    }

    public void show(Dialog dialog, CancelListener listener) {
        showInner(false, dialog, listener);
    }

    private void showInner(boolean allowManager, Dialog dialog, CancelListener listener) {
        if (dialog == null) return;
        try {
            dialog.show();
            boolean contains = false;
            int size = mStack.size();
            for (int i=0; i<size; i++){
                E e = mStack.get(i);
                if (e.obj == dialog){
                    contains = true;
                    break;
                }
            }
            if (allowManager && !contains) {
                mStack.push(new E(dialog, listener));
            }
        } catch (Exception e) {
            if (listener != null) {
                listener.cancel();
            }
            e.printStackTrace();
        }
    }

    public void dismiss(Dialog dialog) {
        if (dialog == null) return;
        int size = mStack.size();
        for (int i=0; i<size; i++){
            E e = mStack.get(i);
            if (e.obj == dialog){
                mStack.remove(e);
                break;
            }
        }
        if (!dialog.isShowing()) return;
        try {
            dialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void show(AlertDialog dialog, CancelListener listener) {
        showInner(false, dialog, listener);
    }

    private void showInner(boolean allowManager, AlertDialog dialog, CancelListener listener) {
        if (dialog == null) return;
        try {
            dialog.show();
            boolean contains = false;
            int size = mStack.size();
            for (int i=0; i<size; i++){
                E e = mStack.get(i);
                if (e.obj == dialog){
                    contains = true;
                    break;
                }
            }
            if (allowManager && !contains) {
                mStack.push(new E(dialog, listener));
            }
        } catch (Exception e) {
            if (listener != null) {
                listener.cancel();
            }
            e.printStackTrace();
        }
    }

    public void dismiss(AlertDialog dialog) {
        if (dialog == null) return;
        int size = mStack.size();
        for (int i=0; i<size; i++){
            E e = mStack.get(i);
            if (e.obj == dialog){
                mStack.remove(e);
                break;
            }
        }
        if (!dialog.isShowing()) return;
        try {
            dialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dismiss(PopupWindow popup) {
        if (popup == null) return;
        int size = mStack.size();
        for (int i=0; i<size; i++){
            E e = mStack.get(i);
            if (e.obj == popup){
                mStack.remove(e);
                break;
            }
        }
        if (!popup.isShowing()) return;
        try {
            popup.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeAllDialog() {
        while (!mStack.isEmpty()) {
            E e = mStack.pop();
            if (e == null || e.obj == null) continue;
            if (e.obj instanceof DialogFragment) {
                dismiss((DialogFragment) e.obj);
            } else if (e.obj instanceof Dialog) {
                dismiss((Dialog) e.obj);
            } else if (e.obj instanceof AlertDialog) {
                dismiss((AlertDialog) e.obj);
            } else if (e.obj instanceof PopupWindow) {
                dismiss((PopupWindow) e.obj);
            }
            if (e.listener != null) {
                e.listener.cancel();
            }
        }
    }
}
