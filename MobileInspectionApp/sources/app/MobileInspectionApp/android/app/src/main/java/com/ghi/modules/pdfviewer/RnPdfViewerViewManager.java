package com.ghi.modules.pdfviewer;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.ToastUtils;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.foxit.sdk.PDFViewCtrl;
import com.foxit.sdk.common.Constants;
import com.foxit.sdk.common.Library;
import com.foxit.uiextensions.UIExtensionsManager;
import com.foxit.uiextensions.config.Config;
import com.mobileinspectionapp.R;

import java.io.IOException;
import java.io.InputStream;

/**
 * PDF阅读器视图管理器
 *
 * @author Alex
 */
public final class RnPdfViewerViewManager extends SimpleViewManager<View> {
    /**
     * SN
     */
    private static final String SN = "clgU6VDvO5QSp9JjIjVgnD9gk50ei1Kd+ClH4BtQhuP4jWF557RqDA==";

    /**
     * KEY
     */
    private static final String KEY = "ezJvjlnatG53NzsCGirXpdq5jdadAYRnZ2UKOn1rmFCXE4tfh4A1R/2BvaDJ6PAAUFgncfPh3UkzYrnWq9BeTW6nSPLqTUteJgWVYYdvhzLebfWUVXS00fg8N6QMFkUNtTtsvUgpVo0Kj66RDOU1ZgIDcU0FMawop0xytctF9CNYLcePvUTHd48W8UbAsKvzDEkD4b7C6zqBdAqcVUvBjEyIhVTq1SxiQljkCuZb7fiq7HQjoYuZbsiVOxPQpprmqGW4sDyY/a6xaYNEgjCudhZ6b23qoQQkxzaPrSb18vxEhq+hrhH9HDLm9LOWuqUZHAGr7kEXpzqitCqOBcCoxyVBw3sw4XpRqdFQ7lDdKuLQRX46fcCMA6W4dOnwOC6qfSobaf3V/KmQ36xj+hv6G9Fa4ziEf5SutEmz7BxreRkgMg+ExeTQJlLEHNmgdricp3eOh7wWYEUJTMuN/j0iCjrzPJfvse2icduMrQLOLa0eSVrNL2zGYBIRNMvk+ZACO5QjgQOi0c9PRAkqVb2huMbAnPNZPKm4Z9F6cFvaGiiRqzC6yMivVcu/j6akCjax/eWKTc9Bhp8sh2f5jJQRdjNKT0gLP3HeB5QuturafcjMDI0biN/yaN+gRb+sXGETysID7RcxVSUTKjL3xL9U/Rj25qlgcUI3VUWFQNMHCnOFuzGB98qUyTVm5mHjURK/cR4l0vuiMqrh1uXg5neckcpsiIqieuGZXhH2TN/CF6Doy/B/U36SoBPLX9//XigQtoP1RZqTIYNilOHK4KjpdxVMuqR2kxSjV7+/yAtoyMbGxmXyFcuG3RYTXFZF2kq5VxNuozl4x1f0Ki+R6ILhoaRZjZzmnLFbvjCc+wypOQGWBQA0pwU2NFqyXWmbrpopN1NAVfBRtn18xK24ia5+p7fPpFkIEmMO45a1LvJ6FxzJEkBP9hidOf+tR5YbQEAa1fHqdS3iQj3sOAhbqgqppegi7oAaZypYesd1yEKAjxxSPltK3tzwF8Q5JXygBRgqNQ7hyiQJH8Dviwm4cr2CTuU1Ok0eACfQhLjpgmFPxlSGyGX3KOYWNdh1Q1v7lbDa4gkDyjZeUoy86BR2OI4AO+eiz0SGiLmf4a2Kw0sBuNYIXd8rQ9H4jO0u3nLkLvBwMZ3szlfKMiGeBpZDIGGuqvkebshKAoy+iDixtHJSqb6tYOiVlE6uRB3ufciz+XXOtoKYs9kn/9glcPVdKiv3ysqU2iQ3zZNnhhFIIu3HXgRF/LjlMeHZ2coz46IsRqVz";

    /**
     * 上下文
     */
    private final ReactApplicationContext reactContext;

    /**
     * 构造函数
     *
     * @param reactContext 上下文
     */
    public RnPdfViewerViewManager(final ReactApplicationContext reactContext) {
        this.reactContext = reactContext;
    }

    @NonNull
    @Override
    public String getName() {
        return "_PdfViewer";
    }

    @NonNull
    @Override
    protected View createViewInstance(@NonNull ThemedReactContext reactContext) {
        return createView(reactContext.getCurrentActivity());
    }

    /**
     * 创建视图
     *
     * @param activity 界面
     * @return 视图
     */
    private View createView(final Activity activity) {
        int errorCode = Library.initialize(SN, KEY);
        if (errorCode != Constants.e_ErrSuccess) {
            ToastUtils.showLong("SN或KEY错误!");
            return new EmptyView(activity);
        }

        PDFViewCtrl pdfViewCtrl = new PDFViewCtrl(activity);
        pdfViewCtrl.setAttachedActivity(activity);

        try (InputStream stream = activity.getResources().openRawResource(R.raw.uiextensions_config)) {
            Config config = new Config(stream);
            UIExtensionsManager uiExtensionsManager = new UIExtensionsManager(activity, pdfViewCtrl, config);
            uiExtensionsManager.setAttachedActivity(activity);
            uiExtensionsManager.onCreate(activity, pdfViewCtrl, null);
            pdfViewCtrl.setUIExtensionsManager(uiExtensionsManager);

            uiExtensionsManager.enableTopToolbar(false);
            uiExtensionsManager.enableBottomToolbar(false);

            View view = uiExtensionsManager.getContentView();
            view.setTag(new PdfViewer(reactContext, activity, uiExtensionsManager));
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            view.setLayoutParams(layoutParams);

            return view;
        } catch (IOException e) {
            return new EmptyView(activity);
        }
    }

    /**
     * 空视图
     */
    static class EmptyView extends View {
        public EmptyView(Context context) {
            super(context);
        }
    }
}
