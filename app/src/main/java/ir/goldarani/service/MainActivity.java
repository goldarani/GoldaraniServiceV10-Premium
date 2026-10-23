package ir.goldarani.service;

import android.app.*;
import android.os.Bundle;
import android.content.*;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.net.Uri;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.text.InputType;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MainActivity extends Activity {
    private final int NAVY = Color.rgb(6,16,29);
    private final int PANEL = Color.rgb(12,29,47);
    private final int PANEL2 = Color.rgb(19,42,63);
    private final int GOLD = Color.rgb(229,177,72);
    private final int WHITE = Color.rgb(246,248,252);
    private final int MUTED = Color.rgb(157,176,196);
    private final int BLUE = Color.rgb(29,55,79);
    private final int TEAL = Color.rgb(36,105,93);
    private final int ORANGE = Color.rgb(178,119,41);
    private final int PURPLE = Color.rgb(88,102,124);
    private Db db;
    private String userName = "", userPhone = "";
    private boolean dark = true;
    private float fontScale = 1.0f;
    private LinearLayout root, content;
    private int entranceIndex = 0;
    private String page = "home";
    private Typeface iranSans;

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().setStatusBarColor(NAVY);
        getWindow().setNavigationBarColor(NAVY);
        db = new Db(this);
        try { iranSans = Typeface.createFromAsset(getAssets(), "fonts/IRANSans.ttf"); }
        catch (Exception ignored) { iranSans = Typeface.create("sans-serif", Typeface.NORMAL); }
        android.content.SharedPreferences p = getSharedPreferences("profile", MODE_PRIVATE);
        userName = p.getString("name", "");
        userPhone = p.getString("phone", "");
        dark = p.getBoolean("dark", true);
        fontScale = p.getFloat("font", 1.0f);
        if (userName.trim().isEmpty()) showLogin(); else showHome();
    }

    private int bg() { return dark ? NAVY : Color.rgb(239,244,250); }
    private int panel() { return dark ? PANEL : Color.WHITE; }
    private int txt() { return dark ? WHITE : Color.rgb(20,37,55); }
    private int muted() { return dark ? MUTED : Color.rgb(90,108,126); }

    private GradientDrawable shape(int color, int radius, int stroke) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(dp(radius));
        if (stroke != 0) d.setStroke(dp(1), stroke);
        return d;
    }

    private GradientDrawable gradientShape(int startColor, int endColor, int radius, int stroke) {
        GradientDrawable d = new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                new int[]{startColor, endColor});
        d.setCornerRadius(dp(radius));
        if (stroke != 0) d.setStroke(dp(1), stroke);
        return d;
    }

    private android.graphics.drawable.Drawable ripple(GradientDrawable base) {
        int rippleColor = dark ? Color.argb(38, 245, 198, 105) : Color.argb(28, 20, 45, 70);
        return new RippleDrawable(ColorStateList.valueOf(rippleColor), base, null);
    }

    private void animateIn(View view) {
        int delay = Math.min(entranceIndex * 22, 132);
        entranceIndex++;
        view.setAlpha(0f);
        view.setTranslationY(dp(8));
        view.animate().alpha(1f).translationY(0f).setDuration(260).setStartDelay(delay).start();
    }

    private TextView text(String s, int size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(s); t.setTextColor(color); t.setTextSize(size * fontScale);
        t.setTextDirection(View.TEXT_DIRECTION_RTL);
        t.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        t.setTypeface(iranSans != null ? Typeface.create(iranSans, bold ? Typeface.BOLD : Typeface.NORMAL)
                                       : Typeface.create("sans-serif", bold ? Typeface.BOLD : Typeface.NORMAL));
        t.setPadding(dp(2), dp(3), dp(2), dp(3));
        t.setLineSpacing(dp(1), 1.02f);
        return t;
    }

    private int dp(float x) { return (int)(x * getResources().getDisplayMetrics().density + 0.5f); }

    private LinearLayout column() {
        LinearLayout l = new LinearLayout(this); l.setOrientation(LinearLayout.VERTICAL); return l;
    }

    private LinearLayout row() {
        LinearLayout l = new LinearLayout(this); l.setOrientation(LinearLayout.HORIZONTAL);
        l.setGravity(Gravity.CENTER_VERTICAL); return l;
    }

private void setup(String title) {
        entranceIndex = 0;
        root = column();
        root.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        root.setBackground(gradientShape(
                dark ? Color.rgb(5, 14, 26) : Color.rgb(235, 242, 249),
                dark ? Color.rgb(8, 24, 39) : Color.rgb(248, 250, 253), 0, 0));

        if (android.os.Build.VERSION.SDK_INT >= 23) {
            getWindow().setStatusBarColor(dark ? NAVY : Color.rgb(235, 242, 249));
            getWindow().setNavigationBarColor(dark ? NAVY : Color.rgb(235, 242, 249));
            getWindow().getDecorView().setSystemUiVisibility(dark ? 0 :
                    (View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR));
        }

        LinearLayout bar = row();
        bar.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        bar.setPadding(dp(12), dp(11), dp(13), dp(11));
        bar.setBackground(gradientShape(
                dark ? Color.rgb(19, 42, 63) : Color.WHITE,
                dark ? Color.rgb(10, 25, 42) : Color.rgb(244, 248, 252), 22,
                dark ? Color.rgb(43, 68, 89) : Color.rgb(213, 225, 237)));
        bar.setElevation(dp(3));

        TextView back = text("‹", 28, GOLD, true);
        back.setGravity(Gravity.CENTER);
        back.setBackground(ripple(shape(dark ? Color.rgb(24, 48, 70) : Color.rgb(245, 248, 252), 14,
                dark ? Color.rgb(59, 79, 97) : Color.rgb(216, 226, 237))));
        bar.addView(back, new LinearLayout.LayoutParams(dp(40), dp(40)));
        back.setOnClickListener(v -> showHome());

        LinearLayout titleStack = column();
        titleStack.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        titleStack.setPadding(dp(12), 0, dp(9), 0);
        TextView en = text("G O L D A R A N I   •   E L E V A T O R", 8, GOLD, true);
        en.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        titleStack.addView(en);
        TextView fa = text(title, 17, txt(), true);
        fa.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        titleStack.addView(fa);
        bar.addView(titleStack, new LinearLayout.LayoutParams(0, -2, 1));

        TextView mark = text("G", 23, NAVY, true);
        mark.setGravity(Gravity.CENTER);
        mark.setBackground(ripple(gradientShape(Color.rgb(248, 211, 125), GOLD, 15, Color.rgb(255, 231, 176))));
        mark.setElevation(dp(2));
        bar.addView(mark, new LinearLayout.LayoutParams(dp(43), dp(43)));

        LinearLayout.LayoutParams barLp = new LinearLayout.LayoutParams(-1, -2);
        barLp.setMargins(dp(12), dp(9), dp(12), dp(8));
        root.addView(bar, barLp);

        ScrollView sc = new ScrollView(this);
        sc.setFillViewport(false);
        sc.setClipToPadding(false);
        sc.setVerticalScrollBarEnabled(false);
        sc.setOverScrollMode(View.OVER_SCROLL_NEVER);
        content = column();
        content.setPadding(dp(16), dp(11), dp(16), dp(22));
        sc.addView(content);
        root.addView(sc, new LinearLayout.LayoutParams(-1, 0, 1));

        if (!"ورود به سرویس‌یار".equals(title)) addBottomNav();
        setContentView(root);
    }

    private TextView button(String label, int color, Runnable action) {
        boolean primary = color == GOLD;
        TextView b = text(label, 13, primary ? NAVY : txt(), true);
        b.setGravity(Gravity.CENTER);
        b.setMinHeight(dp(46));
        b.setPadding(dp(15), dp(11), dp(15), dp(11));
        GradientDrawable base = primary
                ? gradientShape(Color.rgb(247, 211, 128), GOLD, 15, Color.rgb(255, 229, 169))
                : gradientShape(dark ? Color.rgb(20, 43, 64) : Color.rgb(251, 253, 255),
                    dark ? Color.rgb(13, 30, 48) : Color.rgb(241, 246, 251), 15,
                    dark ? Color.rgb(43, 69, 91) : Color.rgb(210, 223, 235));
        b.setBackground(ripple(base));
        if (primary) b.setElevation(dp(2));
        b.setOnClickListener(v -> action.run());
        return b;
    }

    private void addButton(String label, int color, Runnable action) {
        TextView b = button(label, color, action);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(0, dp(5), 0, dp(5));
        content.addView(b, lp);
        animateIn(b);
    }

    private void card(LinearLayout parent, View child) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(0, dp(5), 0, dp(8));
        child.setPadding(dp(16), dp(15), dp(16), dp(15));
        if (child.getBackground() == null) {
            child.setBackground(gradientShape(
                    dark ? Color.rgb(17, 39, 59) : Color.WHITE,
                    dark ? Color.rgb(11, 28, 45) : Color.rgb(248, 251, 254), 20,
                    dark ? Color.rgb(35, 61, 82) : Color.rgb(215, 226, 237)));
        }
        child.setElevation(dp(2));
        parent.addView(child, lp);
        animateIn(child);
    }

    private EditText edit(String hint, String value, int inputType) {
        EditText e = new EditText(this);
        e.setText(value);
        e.setHint(hint);
        e.setTextColor(txt());
        e.setHintTextColor(muted());
        e.setTextSize(14 * fontScale);
        e.setTypeface(iranSans != null ? iranSans : Typeface.create("sans-serif", Typeface.NORMAL));
        e.setInputType(inputType);
        boolean multiline = (inputType & InputType.TYPE_TEXT_FLAG_MULTI_LINE) != 0;
        e.setSingleLine(!multiline);
        if (multiline) e.setMinLines(2);
        e.setGravity(Gravity.RIGHT | (multiline ? Gravity.TOP : Gravity.CENTER_VERTICAL));
        e.setTextDirection(View.TEXT_DIRECTION_RTL);
        e.setPadding(dp(14), dp(12), dp(14), dp(12));
        e.setBackground(gradientShape(dark ? Color.rgb(7, 21, 36) : Color.rgb(249, 251, 254),
                dark ? Color.rgb(10, 28, 45) : Color.WHITE, 14,
                dark ? Color.rgb(41, 66, 87) : Color.rgb(203, 218, 232)));
        e.setOnFocusChangeListener((v, hasFocus) -> {
            int stroke = hasFocus ? GOLD : (dark ? Color.rgb(41, 66, 87) : Color.rgb(203, 218, 232));
            e.setBackground(gradientShape(dark ? Color.rgb(7, 21, 36) : Color.rgb(249, 251, 254),
                    dark ? Color.rgb(10, 28, 45) : Color.WHITE, 14, stroke));
            if (hasFocus) e.animate().translationY(-dp(1)).setDuration(120).start();
            else e.animate().translationY(0).setDuration(120).start();
        });
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(0, dp(5), 0, dp(8));
        e.setLayoutParams(lp);
        return e;
    }

    private void showLogin() {
        page = "login";
        setup("ورود به سرویس‌یار");
        LinearLayout hero = column();
        hero.setGravity(Gravity.CENTER);
        hero.setPadding(dp(20), dp(25), dp(20), dp(23));
        hero.setBackground(gradientShape(dark ? Color.rgb(24, 52, 76) : Color.WHITE,
                dark ? Color.rgb(8, 23, 39) : Color.rgb(238, 245, 251), 24,
                dark ? Color.rgb(67, 84, 96) : Color.rgb(213, 225, 237)));
        TextView icon = text("G", 43, NAVY, true);
        icon.setGravity(Gravity.CENTER);
        icon.setBackground(ripple(gradientShape(Color.rgb(250, 220, 147), GOLD, 22, Color.rgb(255, 233, 185))));
        icon.setElevation(dp(4));
        LinearLayout.LayoutParams ip = new LinearLayout.LayoutParams(dp(88), dp(88));
        ip.gravity = Gravity.CENTER;
        hero.addView(icon, ip);
        TextView title = text("سرویس‌یار گلدرانی", 24, GOLD, true);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, dp(14), 0, dp(4));
        hero.addView(title);
        TextView sub = text("مدیریت حرفه‌ای سرویس و نگهداری آسانسور", 13, muted(), false);
        sub.setGravity(Gravity.CENTER);
        hero.addView(sub);
        View divider = new View(this);
        divider.setBackgroundColor(dark ? Color.rgb(61, 79, 96) : Color.rgb(216, 226, 237));
        LinearLayout.LayoutParams divLp = new LinearLayout.LayoutParams(dp(46), dp(3));
        divLp.gravity = Gravity.CENTER;
        divLp.setMargins(0, dp(15), 0, dp(3));
        hero.addView(divider, divLp);
        card(content, hero);

        LinearLayout form = column();
        form.addView(text("ورود به حساب کاربری", 17, txt(), true));
        form.addView(text("برای شروع، نام و شماره تماس را وارد کن.", 12, muted(), false));
        EditText name = edit("نام شما", "", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        EditText phone = edit("شماره تماس", "", InputType.TYPE_CLASS_PHONE);
        form.addView(name); form.addView(phone);
        TextView submit = button("ورود به برنامه   ←", GOLD, () -> {
            String n = name.getText().toString().trim(), p = phone.getText().toString().trim();
            if (n.isEmpty() || p.isEmpty()) { toast("نام و شماره تماس را وارد کنید"); return; }
            userName = n; userPhone = p;
            getSharedPreferences("profile", MODE_PRIVATE).edit().putString("name", n).putString("phone", p).apply();
            hideKeyboard(); showHome();
        });
        LinearLayout.LayoutParams blp = new LinearLayout.LayoutParams(-1, -2);
        blp.setMargins(0, dp(9), 0, dp(3));
        form.addView(submit, blp);
        card(content, form);
        TextView foot = text("اطلاعات شما فقط روی همین دستگاه ذخیره می‌شود", 11, muted(), false);
        foot.setGravity(Gravity.CENTER);
        foot.setPadding(0, dp(6), 0, dp(12));
        content.addView(foot);
    }

    private void showHome() {
        page = "home";
        setup("داشبورد");

        // Compact cinematic hero: clear greeting, brand identity, and useful date.
        LinearLayout hero = row();
        hero.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        hero.setGravity(Gravity.CENTER_VERTICAL);
        hero.setPadding(dp(17), dp(18), dp(17), dp(18));
        hero.setBackground(gradientShape(dark ? Color.rgb(25, 51, 73) : Color.WHITE,
                dark ? Color.rgb(8, 23, 39) : Color.rgb(238, 245, 251), 24,
                dark ? Color.rgb(66, 82, 97) : Color.rgb(213, 225, 237)));
        hero.setElevation(dp(5));

        LinearLayout logoWrap = column();
        logoWrap.setGravity(Gravity.CENTER);
        logoWrap.setPadding(dp(4), dp(4), dp(4), dp(4));
        logoWrap.setBackground(gradientShape(dark ? Color.rgb(36, 57, 73) : Color.rgb(248, 241, 221),
                dark ? Color.rgb(17, 35, 52) : Color.rgb(255, 252, 245), 19,
                dark ? Color.rgb(89, 79, 55) : Color.rgb(235, 214, 166)));
        TextView logo = text("G", 35, GOLD, true);
        logo.setGravity(Gravity.CENTER);
        logo.setShadowLayer(dp(8), 0, 0, Color.argb(60, 246, 204, 115));
        logoWrap.addView(logo, new LinearLayout.LayoutParams(dp(57), dp(61)));
        LinearLayout.LayoutParams logoLp = new LinearLayout.LayoutParams(dp(65), dp(69));
        logoLp.setMargins(0, 0, dp(14), 0);
        hero.addView(logoWrap, logoLp);

        LinearLayout intro = column();
        intro.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        TextView eyebrow = text("ELEVATOR CARE  /  CONTROL CENTER", 8, GOLD, true);
        eyebrow.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        eyebrow.setLetterSpacing(0.07f);
        intro.addView(eyebrow);
        TextView hello = text("سلام، " + userName, 21, txt(), true);
        hello.setMaxLines(1);
        hello.setEllipsize(android.text.TextUtils.TruncateAt.END);
        intro.addView(hello);
        intro.addView(text("سرویس‌ها را منظم، سریع و دقیق مدیریت کن.", 11, muted(), false));
        TextView date = text("●   امروز  " + todayJalali(), 10, GOLD, true);
        date.setPadding(0, dp(7), 0, 0);
        intro.addView(date);
        hero.addView(intro, new LinearLayout.LayoutParams(0, -2, 1));
        card(content, hero);

        addSectionHeading("نمای کلی امروز");
        LinearLayout stats = row();
        stats.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        stat(stats, "سرویس امروز", db.countTodayServices(todayJalali()), GOLD);
        stat(stats, "کل سرویس‌ها", db.count("services"), GOLD);
        content.addView(stats);

        int overdueCount = db.countOverdueServices(todayJalali());
        LinearLayout overdue = row();
        overdue.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        overdue.setGravity(Gravity.CENTER_VERTICAL);
        overdue.setPadding(dp(14), dp(13), dp(14), dp(13));
        overdue.setBackground(ripple(gradientShape(
                overdueCount > 0 ? (dark ? Color.rgb(68, 35, 43) : Color.rgb(255, 241, 239)) :
                        (dark ? Color.rgb(17, 39, 57) : Color.rgb(255, 255, 255)),
                dark ? Color.rgb(12, 28, 44) : Color.rgb(245, 249, 253), 18,
                overdueCount > 0 ? Color.rgb(145, 69, 74) : (dark ? Color.rgb(41, 66, 86) : Color.rgb(211, 224, 236)))));
        LinearLayout alertText = column();
        alertText.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        alertText.addView(text("سرویس‌های عقب‌افتاده", 13, txt(), true));
        alertText.addView(text(overdueCount > 0 ? "نیازمند پیگیری هستند" : "همه‌چیز مرتب است", 11, muted(), false));
        overdue.addView(alertText, new LinearLayout.LayoutParams(0, -2, 1));
        TextView overdueCountView = text(String.valueOf(overdueCount), 22,
                overdueCount > 0 ? Color.rgb(255, 151, 148) : GOLD, true);
        overdueCountView.setGravity(Gravity.CENTER);
        overdueCountView.setBackground(shape(overdueCount > 0 ? Color.rgb(91, 43, 51) :
                (dark ? Color.rgb(31, 52, 69) : Color.rgb(250, 243, 223)), 14, 0));
        overdue.addView(overdueCountView, new LinearLayout.LayoutParams(dp(48), dp(45)));
        LinearLayout.LayoutParams overdueLp = new LinearLayout.LayoutParams(-1, -2);
        overdueLp.setMargins(0, dp(10), 0, dp(4));
        content.addView(overdue, overdueLp);
        overdue.setOnClickListener(v -> showServiceList("overdue"));
        animateIn(overdue);

        LinearLayout sectionRow = row();
        sectionRow.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        TextView quickTitle = text("دسترسی سریع", 17, txt(), true);
        sectionRow.addView(quickTitle, new LinearLayout.LayoutParams(0, -2, 1));
        TextView quickCount = text("۶ ابزار", 10, GOLD, true);
        quickCount.setGravity(Gravity.CENTER);
        quickCount.setPadding(dp(10), dp(6), dp(10), dp(6));
        quickCount.setBackground(shape(dark ? Color.rgb(36, 48, 56) : Color.rgb(250, 243, 224), 10,
                dark ? Color.rgb(83, 72, 48) : Color.rgb(236, 219, 173)));
        sectionRow.addView(quickCount);
        LinearLayout.LayoutParams secLp = new LinearLayout.LayoutParams(-1, -2);
        secLp.setMargins(0, dp(17), 0, dp(7));
        content.addView(sectionRow, secLp);

        LinearLayout grid1 = row(); grid1.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        gridTile(grid1, "", "ثبت پرونده ساختمان", GOLD, () -> showForm("services"));
        gridTile(grid1, "", "سرویس‌های امروز", GOLD, () -> showServiceList("today"));
        content.addView(grid1);
        LinearLayout grid2 = row(); grid2.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        gridTile(grid2, "", "انجام‌شده‌ها", GOLD, () -> showServiceList("completed"));
        gridTile(grid2, "", "همه پرونده‌ها", GOLD, () -> showServiceList("all"));
        content.addView(grid2);
        LinearLayout grid3 = row(); grid3.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        gridTile(grid3, "", "طلب‌ها", GOLD, () -> showList("debts"));
        gridTile(grid3, "", "تنظیمات", GOLD, () -> showSettings());
        content.addView(grid3);

        LinearLayout debtCard = row();
        debtCard.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        debtCard.setGravity(Gravity.CENTER_VERTICAL);
        debtCard.setPadding(dp(15), dp(13), dp(15), dp(13));
        debtCard.setBackground(gradientShape(dark ? Color.rgb(18, 42, 61) : Color.WHITE,
                dark ? Color.rgb(10, 27, 44) : Color.rgb(245, 249, 253), 20,
                dark ? Color.rgb(51, 74, 89) : Color.rgb(214, 226, 237)));
        LinearLayout debtText = column();
        debtText.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        debtText.addView(text("مجموع طلب‌های ثبت‌شده", 12, muted(), true));
        TextView debtAmount = text(money(db.totalDebts()) + " تومان", 19, GOLD, true);
        debtText.addView(debtAmount);
        debtCard.addView(debtText, new LinearLayout.LayoutParams(0, -2, 1));
        IconView debtIcon = new IconView("debts", GOLD);
        debtIcon.setBackground(shape(dark ? Color.rgb(37, 49, 55) : Color.rgb(251, 244, 228), 15,
                dark ? Color.rgb(83, 73, 49) : Color.rgb(237, 222, 184)));
        debtCard.addView(debtIcon, new LinearLayout.LayoutParams(dp(48), dp(48)));
        LinearLayout.LayoutParams debtLp = new LinearLayout.LayoutParams(-1, -2);
        debtLp.setMargins(0, dp(12), 0, dp(5));
        content.addView(debtCard, debtLp);
        debtCard.setOnClickListener(v -> showList("debts"));
        animateIn(debtCard);

        LinearLayout support = row();
        support.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        support.setGravity(Gravity.CENTER_VERTICAL);
        support.setPadding(dp(13), dp(12), dp(13), dp(12));
        support.setBackground(shape(dark ? Color.rgb(11, 26, 42) : Color.rgb(247, 250, 253), 17,
                dark ? Color.rgb(34, 56, 75) : Color.rgb(216, 227, 238)));
        LinearLayout supportText = column();
        supportText.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        supportText.addView(text("پشتیبانی گلدرانی", 12, txt(), true));
        supportText.addView(text("0912-468-2580", 12, GOLD, true));
        support.addView(supportText, new LinearLayout.LayoutParams(0, -2, 1));
        TextView call = text("تماس", 11, NAVY, true);
        call.setGravity(Gravity.CENTER);
        call.setPadding(dp(14), dp(9), dp(14), dp(9));
        call.setBackground(ripple(gradientShape(Color.rgb(247, 211, 128), GOLD, 12, 0)));
        support.addView(call);
        LinearLayout.LayoutParams supportLp = new LinearLayout.LayoutParams(-1, -2);
        supportLp.setMargins(0, dp(3), 0, dp(7));
        content.addView(support, supportLp);
        call.setOnClickListener(v -> dial("09124682580"));
        support.setOnClickListener(v -> dial("09124682580"));
        animateIn(support);
    }

private void addSectionHeading(String title) {
        LinearLayout wrap = row();
        wrap.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        View accent = new View(this);
        accent.setBackground(gradientShape(Color.rgb(255, 220, 135), GOLD, 3, 0));
        wrap.addView(accent, new LinearLayout.LayoutParams(dp(3), dp(23)));
        TextView label = text(title, 16, txt(), true);
        LinearLayout.LayoutParams tlp = new LinearLayout.LayoutParams(0, -2, 1);
        tlp.setMargins(dp(9), 0, 0, 0);
        wrap.addView(label, tlp);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(0, dp(16), 0, dp(8));
        content.addView(wrap, lp);
    }

    private void gridTile(LinearLayout parent, String emoji, String label, int color, Runnable click) {
        LinearLayout tile = column();
        tile.setGravity(Gravity.CENTER_VERTICAL);
        tile.setPadding(dp(11), dp(12), dp(11), dp(12));
        tile.setBackground(ripple(gradientShape(
                dark ? Color.rgb(19, 42, 62) : Color.WHITE,
                dark ? Color.rgb(11, 27, 44) : Color.rgb(246, 249, 253), 18,
                dark ? Color.rgb(39, 64, 84) : Color.rgb(211, 224, 237))));
        tile.setElevation(dp(1));

        LinearLayout inner = row();
        inner.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        inner.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout words = column();
        words.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        TextView tx = text(label, 12, txt(), true);
        tx.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        tx.setMaxLines(2);
        tx.setLineSpacing(dp(1), 1.0f);
        words.addView(tx);
        TextView desc = text(tileSubtitle(label), 9, muted(), false);
        desc.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        desc.setMaxLines(1);
        desc.setEllipsize(android.text.TextUtils.TruncateAt.END);
        words.addView(desc);
        inner.addView(words, new LinearLayout.LayoutParams(0, -2, 1));

        LinearLayout iconBox = column();
        iconBox.setGravity(Gravity.CENTER);
        iconBox.setBackground(gradientShape(dark ? Color.rgb(38, 52, 63) : Color.rgb(250, 244, 228),
                dark ? Color.rgb(25, 41, 55) : Color.rgb(255, 251, 242), 13,
                dark ? Color.rgb(79, 72, 54) : Color.rgb(235, 219, 177)));
        IconView ic = new IconView(label, GOLD);
        iconBox.addView(ic, new LinearLayout.LayoutParams(dp(25), dp(25)));
        LinearLayout.LayoutParams iconLp = new LinearLayout.LayoutParams(dp(39), dp(39));
        iconLp.setMargins(dp(8), 0, 0, 0);
        inner.addView(iconBox, iconLp);
        tile.addView(inner, new LinearLayout.LayoutParams(-1, -2));

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, dp(83), 1);
        lp.setMargins(dp(4), dp(4), dp(4), dp(4));
        parent.addView(tile, lp);
        tile.setOnClickListener(v -> {
            v.animate().scaleX(0.985f).scaleY(0.985f).setDuration(75).withEndAction(() -> {
                v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                click.run();
            }).start();
        });
        animateIn(tile);
    }

    private String tileSubtitle(String label) {
        if (label.contains("ثبت پرونده")) return "ساختمان و سرویس جدید";
        if (label.contains("امروز")) return "برنامهٔ امروز";
        if (label.contains("انجام")) return "سوابق تکمیل‌شده";
        if (label.contains("همه")) return "مرور سرویس‌ها";
        if (label.contains("طلب")) return "پیگیری مبالغ";
        if (label.contains("تنظیمات")) return "شخصی‌سازی برنامه";
        return "مشاهدهٔ جزئیات";
    }

    private void stat(LinearLayout parent, String label, int count, int color) {
        LinearLayout box = column();
        box.setGravity(Gravity.CENTER);
        box.setPadding(dp(10), dp(11), dp(10), dp(11));
        box.setBackground(gradientShape(dark ? Color.rgb(17, 39, 58) : Color.WHITE,
                dark ? Color.rgb(11, 27, 43) : Color.rgb(246, 249, 253), 17,
                dark ? Color.rgb(38, 62, 82) : Color.rgb(213, 225, 237)));
        TextView n = text(String.valueOf(count), 23, GOLD, true);
        n.setGravity(Gravity.CENTER);
        box.addView(n);
        View accent = new View(this);
        accent.setBackground(shape(GOLD, 3, 0));
        LinearLayout.LayoutParams ap = new LinearLayout.LayoutParams(dp(27), dp(2));
        ap.gravity = Gravity.CENTER;
        ap.setMargins(0, dp(4), 0, dp(4));
        box.addView(accent, ap);
        TextView l = text(label, 10, muted(), true);
        l.setGravity(Gravity.CENTER);
        l.setMaxLines(2);
        box.addView(l);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, dp(82), 1);
        lp.setMargins(dp(4), 0, dp(4), 0);
        parent.addView(box, lp);
        animateIn(box);
    }

    private void addBottomNav() {
        LinearLayout nav = row();
        nav.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        nav.setPadding(dp(7), dp(6), dp(7), dp(6));
        nav.setBackground(gradientShape(dark ? Color.rgb(14, 31, 48) : Color.WHITE,
                dark ? Color.rgb(7, 19, 33) : Color.rgb(246, 249, 253), 22,
                dark ? Color.rgb(45, 68, 87) : Color.rgb(213, 225, 237)));
        nav.setElevation(dp(7));
        navItem(nav, "home", "خانه", page.equals("home"), () -> showHome());
        navItem(nav, "files", "پرونده‌ها", page.equals("services") || page.equals("buildings"), () -> showServiceList("all"));
        navItem(nav, "register", "ثبت", page.equals("register"), () -> showForm("services"));
        navItem(nav, "debts", "طلب‌ها", page.equals("debts"), () -> showList("debts"));
        navItem(nav, "settings", "تنظیمات", page.equals("settings"), () -> showSettings());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(dp(12), dp(5), dp(12), dp(9));
        root.addView(nav, lp);
    }

    private void navItem(LinearLayout nav, String icon, String label, boolean selected, Runnable action) {
        LinearLayout item = column();
        item.setGravity(Gravity.CENTER);
        item.setPadding(dp(3), dp(5), dp(3), dp(4));
        item.setBackground(ripple(shape(selected ? (dark ? Color.rgb(31, 48, 61) : Color.rgb(250, 244, 230)) : Color.TRANSPARENT,
                14, selected ? (dark ? Color.rgb(91, 78, 51) : Color.rgb(237, 220, 178)) : Color.TRANSPARENT)));
        IconView mark = new IconView(icon, selected ? GOLD : muted());
        item.addView(mark, new LinearLayout.LayoutParams(dp(23), dp(23)));
        TextView title = text(label, 10, selected ? GOLD : muted(), selected);
        title.setGravity(Gravity.CENTER);
        title.setMaxLines(1);
        item.addView(title);
        View indicator = new View(this);
        indicator.setBackground(shape(GOLD, 3, 0));
        LinearLayout.LayoutParams indicatorLp = new LinearLayout.LayoutParams(dp(16), dp(2));
        indicatorLp.gravity = Gravity.CENTER;
        indicatorLp.topMargin = dp(3);
        if (selected) item.addView(indicator, indicatorLp);
        nav.addView(item, new LinearLayout.LayoutParams(0, -2, 1));
        item.setOnClickListener(v -> action.run());
    }

    private void showForm(String type) {
    page = "register";
    setup("ثبت ساختمان و سرویس");
    TextView intro = text("ثبت یک‌جای مشخصات ساختمان و برنامهٔ سرویس", 13, muted(), false);
    intro.setPadding(dp(2), 0, dp(2), dp(8));
    content.addView(intro);

    LinearLayout building = column();
    building.addView(text("مشخصات ساختمان", 15, GOLD, true));
    EditText title = edit("نام ساختمان *", "", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
    EditText address = edit("آدرس کامل ساختمان *", "", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
    EditText manager = edit("نام مدیر ساختمان *", "", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
    EditText phone = edit("شماره تماس مدیر *", "", InputType.TYPE_CLASS_PHONE);
    building.addView(title); building.addView(address); building.addView(manager); building.addView(phone);
    card(content, building);

    LinearLayout service = column();
    service.addView(text("اطلاعات سرویس", 15, GOLD, true));
    EditText date = edit("تاریخ سرویس شمسی (1405/07/01)", todayJalali(), InputType.TYPE_CLASS_TEXT);
    EditText serviceAmount = edit("مبلغ سرویس دوره‌ای (تومان)", "", InputType.TYPE_CLASS_NUMBER);
    final int[] recurrenceMonths = new int[]{1}; // سرویس نگهداری به‌صورت پیش‌فرض ماهانه است.
    service.addView(date);
    service.addView(serviceAmount);
    final TextView[] recurrenceChoice = new TextView[1];
    recurrenceChoice[0] = button("تکرار خودکار: هر ماه  ▾", PANEL2, () -> {
        String[] options = {"هر ماه", "هر ۲ ماه", "هر ۳ ماه", "فقط همین سرویس"};
        int selected = recurrenceMonths[0] == 1 ? 0 : recurrenceMonths[0] == 2 ? 1 : recurrenceMonths[0] == 3 ? 2 : 3;
        new AlertDialog.Builder(this).setTitle("فاصلهٔ سرویس دوره‌ای")
                .setSingleChoiceItems(options, selected, (dialog, which) -> {
                    recurrenceMonths[0] = which == 3 ? 0 : which + 1;
                    recurrenceChoice[0].setText("تکرار خودکار: " + options[which] + "  ▾");
                    dialog.dismiss();
                }).setNegativeButton("انصراف", null).show();
    });
    service.addView(text("زمان‌بندی تکرار سرویس", 12, muted(), true));
    service.addView(recurrenceChoice[0]);
    service.addView(text("بعد از تکمیل سرویس، سررسید بعدی با همین مبلغ ثبت می‌شود.", 11, muted(), false));
    EditText repairDetails = edit("شرح خرابی / تعمیرات (اختیاری)", "", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
    EditText repairAmount = edit("مبلغ تعمیرات (تومان)", "", InputType.TYPE_CLASS_NUMBER);
    EditText notes = edit("توضیحات تکمیلی (اختیاری)", "", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
    service.addView(repairDetails); service.addView(repairAmount); service.addView(notes);
    card(content, service);

    addButton("ثبت اطلاعات ساختمان و سرویس  ✓", GOLD, () -> {
        String a = title.getText().toString().trim();
        String addr = address.getText().toString().trim();
        String mgr = manager.getText().toString().trim();
        String ph = phone.getText().toString().trim();
        String dt = date.getText().toString().trim();
        if (a.isEmpty()) { toast("نام ساختمان را وارد کنید"); return; }
        if (addr.isEmpty()) { toast("آدرس ساختمان را وارد کنید"); return; }
        if (mgr.isEmpty()) { toast("نام مدیر ساختمان را وارد کنید"); return; }
        if (ph.isEmpty()) { toast("شماره مدیر ساختمان را وارد کنید"); return; }
        if (!validDate(dt)) { toast("تاریخ را به شکل 1405/07/01 وارد کنید"); return; }
        long serviceCost = parseAmount(serviceAmount.getText().toString().trim());
        long repairCost = parseAmount(repairAmount.getText().toString().trim());
        String desc = notes.getText().toString().trim();
        db.insertService(a, normalizeJalaliDate(dt), desc, serviceCost,
                repairDetails.getText().toString().trim(), repairCost, addr, mgr, ph, recurrenceMonths[0]);
        hideKeyboard();
        toast(recurrenceMonths[0] > 0 ? "ساختمان ثبت شد؛ تکرار خودکار فعال است" : "اطلاعات ساختمان و سرویس ثبت شد");
        showServiceList("pending");
    });
    addButton("مشاهدهٔ پرونده‌ها و سرویس‌ها", PANEL2, () -> showServiceList("all"));
}

    private void showList(String type) {
    if (type.equals("services")) { showServiceList("all"); return; }
    page = type;
    String title = type.equals("services") ? "سرویس‌ها" : type.equals("buildings") ? "ساختمان‌ها" : "طلب‌ها";
    setup(title);
    LinearLayout titleRow = row();
    titleRow.addView(text("فهرست " + title, 17, txt(), true), new LinearLayout.LayoutParams(0, -2, 1));
    TextView plus = text("+", 24, NAVY, true);
    plus.setGravity(Gravity.CENTER);
    plus.setBackground(shape(GOLD, 15, 0));
    titleRow.addView(plus, new LinearLayout.LayoutParams(dp(42), dp(42)));
    content.addView(titleRow);
    plus.setOnClickListener(v -> { if (type.equals("debts")) showDebtForm(); else showForm(type); });

    if (type.equals("debts")) {
        LinearLayout total = column();
        total.addView(text("مجموع طلب‌های ثبت‌شده", 13, muted(), true));
        total.addView(text(money(db.totalDebts()) + " تومان", 24, GOLD, true));
        card(content, total);
        addButton("ثبت طلب جدید  +", GOLD, () -> showDebtForm());
    }
    Cursor c = db.list(type);
    if (c.getCount() == 0) {
        LinearLayout empty = column(); empty.setGravity(Gravity.CENTER); empty.setPadding(dp(18), dp(27), dp(18), dp(27));
        TextView e = text("—", 30, GOLD, true); e.setGravity(Gravity.CENTER); empty.addView(e);
        TextView msg = text("هنوز موردی ثبت نشده است", 14, muted(), false); msg.setGravity(Gravity.CENTER); empty.addView(msg);
        card(content, empty);
    }
    while (c.moveToNext()) {
        final int id = c.getInt(0);
        String a = c.getString(1), b = c.getString(2), d = c.getString(3);
        LinearLayout item = column();
        item.addView(text(a, 15, txt(), true));
        if (b != null && !b.isEmpty()) item.addView(text(type.equals("debts") ? "مبلغ: " + money(parseAmount(b)) + " تومان" : b,
                13, type.equals("debts") ? GOLD : muted(), type.equals("debts")));
        if (d != null && !d.isEmpty()) item.addView(text(d, 12, muted(), false));
        LinearLayout actions = row();
        actions.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        smallAction(actions, "حذف مورد", Color.rgb(142, 55, 65), () -> confirmDelete(id, type));
        item.addView(actions);
        card(content, item);
    }
    c.close();
    addButton("بازگشت به خانه", PANEL2, () -> showHome());
}

    private void showServiceList(String filter) {
        page = "services";
        String screenTitle = filter.equals("completed") ? "سرویس‌های انجام‌شده" :
                filter.equals("today") ? "سرویس‌های سررسید امروز" :
                filter.equals("overdue") ? "سرویس‌های عقب‌افتاده" :
                filter.equals("pending") ? "سرویس‌های باز" : "پرونده‌های ساختمان و سرویس";
        setup(screenTitle);

        LinearLayout tabsTop = row();
        tabsTop.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        tab(tabsTop, "امروز", filter.equals("today"), () -> showServiceList("today"));
        tab(tabsTop, "عقب‌افتاده", filter.equals("overdue"), () -> showServiceList("overdue"));
        tab(tabsTop, "سرویس‌های باز", filter.equals("pending"), () -> showServiceList("pending"));
        content.addView(tabsTop);
        LinearLayout tabsBottom = row();
        tabsBottom.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        tab(tabsBottom, "انجام‌شده", filter.equals("completed"), () -> showServiceList("completed"));
        tab(tabsBottom, "همهٔ سرویس‌ها", filter.equals("all"), () -> showServiceList("all"));
        content.addView(tabsBottom);

        LinearLayout summary = row();
        summary.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        summary.setGravity(Gravity.CENTER_VERTICAL);
        summary.setPadding(dp(14), dp(12), dp(14), dp(12));
        summary.setBackground(gradientShape(dark ? Color.rgb(18, 40, 59) : Color.WHITE,
                dark ? Color.rgb(10, 26, 42) : Color.rgb(246, 249, 253), 17,
                dark ? Color.rgb(42, 67, 86) : Color.rgb(211, 224, 237)));
        LinearLayout summaryWords = column();
        summaryWords.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        summaryWords.addView(text("برنامهٔ سرویس", 13, txt(), true));
        summaryWords.addView(text("امروز  ·  " + todayJalali(), 11, GOLD, true));
        summary.addView(summaryWords, new LinearLayout.LayoutParams(0, -2, 1));
        TextView summaryNumber = text(String.valueOf(db.countTodayServices(todayJalali())), 20, GOLD, true);
        summaryNumber.setGravity(Gravity.CENTER);
        summaryNumber.setBackground(shape(dark ? Color.rgb(36, 50, 62) : Color.rgb(250, 243, 224), 13,
                dark ? Color.rgb(83, 73, 49) : Color.rgb(237, 220, 178)));
        summary.addView(summaryNumber, new LinearLayout.LayoutParams(dp(45), dp(43)));
        card(content, summary);

        Cursor c = db.serviceList(filter, todayJalali());
        if (c.getCount() == 0) {
            LinearLayout empty = column();
            empty.setGravity(Gravity.CENTER);
            empty.setPadding(dp(18), dp(28), dp(18), dp(28));
            TextView e = text("✓", 27, GOLD, true); e.setGravity(Gravity.CENTER); empty.addView(e);
            String emptyMessage = filter.equals("today") ? "برای امروز سرویسی ثبت نشده است" :
                    filter.equals("overdue") ? "سرویس عقب‌افتاده‌ای ندارید؛ عالی است" :
                    filter.equals("completed") ? "هنوز سرویس انجام‌شده‌ای ثبت نشده است" :
                    filter.equals("pending") ? "سرویس بازی وجود ندارد" : "هنوز سرویسی ثبت نشده است";
            TextView msg = text(emptyMessage, 14, muted(), false); msg.setGravity(Gravity.CENTER); empty.addView(msg);
            card(content, empty);
        }
        while (c.moveToNext()) {
            final int id = c.getInt(0);
            String a = c.getString(1), date = c.getString(2), details = c.getString(3);
            boolean done = c.getInt(4) == 1;
            long serviceCost = c.getLong(5), repairCost = c.getLong(7);
            String repairDetails = c.getString(6), address = c.getString(8), manager = c.getString(9), phone = c.getString(10);
            int recurrenceMonths = c.getInt(11);
            long recurrenceGroup = c.getLong(12);
            String currentToday = todayJalali();
            boolean overdue = !done && date != null && date.compareTo(currentToday) < 0;
            boolean dueToday = !done && date != null && date.equals(currentToday);
            String statusLabel = done ? "انجام شد" : overdue ? "عقب‌افتاده" : dueToday ? "سررسید امروز" : "در انتظار";
            int statusText = done ? Color.rgb(145, 221, 192) : overdue ? Color.rgb(255, 157, 151) : dueToday ? GOLD : muted();
            int statusBg = done ? Color.rgb(17, 64, 58) : overdue ? Color.rgb(83, 37, 44) : dueToday ? Color.rgb(65, 51, 28) :
                    (dark ? Color.rgb(29, 44, 56) : Color.rgb(242, 246, 250));

            LinearLayout item = column();
            LinearLayout titleRow = row();
            titleRow.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            TextView nm = text(a == null ? "بدون نام" : a, 16, txt(), true);
            nm.setMaxLines(2);
            titleRow.addView(nm, new LinearLayout.LayoutParams(0, -2, 1));
            TextView badge = text(statusLabel, 10, statusText, true);
            badge.setGravity(Gravity.CENTER);
            badge.setPadding(dp(9), dp(6), dp(9), dp(6));
            badge.setBackground(shape(statusBg, 10, done ? Color.rgb(40, 107, 89) : overdue ? Color.rgb(131, 65, 73) : 0));
            LinearLayout.LayoutParams badgeLp = new LinearLayout.LayoutParams(-2, -2);
            badgeLp.setMargins(dp(7), 0, 0, 0);
            titleRow.addView(badge, badgeLp);
            item.addView(titleRow);

            View divider = new View(this);
            divider.setBackgroundColor(dark ? Color.rgb(41, 63, 81) : Color.rgb(224, 232, 240));
            LinearLayout.LayoutParams dv = new LinearLayout.LayoutParams(-1, dp(1));
            dv.setMargins(0, dp(10), 0, dp(9));
            item.addView(divider, dv);

            LinearLayout metaRow = row();
            metaRow.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            metaCell(metaRow, "مدیر ساختمان", manager == null || manager.isEmpty() ? "—" : manager);
            metaCell(metaRow, "شماره تماس", phone == null || phone.isEmpty() ? "—" : phone);
            item.addView(metaRow);
            if (address != null && !address.isEmpty()) {
                TextView addressText = text("⌖  " + address, 11, muted(), false);
                addressText.setMaxLines(2);
                item.addView(addressText);
            }
            LinearLayout dueRow = row();
            dueRow.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            dueRow.setPadding(dp(10), dp(8), dp(10), dp(8));
            dueRow.setBackground(shape(dark ? Color.rgb(29, 43, 54) : Color.rgb(250, 245, 231), 12,
                    dark ? Color.rgb(73, 68, 51) : Color.rgb(239, 223, 182)));
            TextView dueLabel = text("سررسید", 11, muted(), true);
            dueRow.addView(dueLabel, new LinearLayout.LayoutParams(0, -2, 1));
            dueRow.addView(text(date == null ? "—" : date, 12, GOLD, true));
            LinearLayout.LayoutParams dueLp = new LinearLayout.LayoutParams(-1, -2);
            dueLp.setMargins(0, dp(7), 0, dp(3));
            item.addView(dueRow, dueLp);

            if (recurrenceMonths > 0) {
                TextView repeat = text("↻  سرویس دوره‌ای " + recurrenceLabel(recurrenceMonths) + " · مبلغ برای دوره‌های بعد حفظ می‌شود", 10, muted(), false);
                item.addView(repeat);
            }
            if (details != null && !details.isEmpty()) item.addView(text("توضیحات: " + details, 11, muted(), false));
            if (repairDetails != null && !repairDetails.isEmpty()) item.addView(text("شرح خرابی: " + repairDetails, 11, muted(), false));

            LinearLayout financial = column();
            financial.setPadding(dp(12), dp(10), dp(12), dp(10));
            financial.setBackground(gradientShape(dark ? Color.rgb(24, 43, 57) : Color.rgb(251, 247, 237),
                    dark ? Color.rgb(15, 31, 45) : Color.rgb(247, 242, 229), 15,
                    dark ? Color.rgb(72, 69, 52) : Color.rgb(237, 222, 186)));
            financial.addView(text("مبلغ سرویس  " + money(serviceCost) + " تومان", 11, muted(), false));
            if (repairCost > 0) financial.addView(text("تعمیرات  " + money(repairCost) + " تومان", 11, muted(), false));
            financial.addView(text("جمع کل  ·  " + money(serviceCost + repairCost) + " تومان", 16, GOLD, true));
            LinearLayout.LayoutParams financialLp = new LinearLayout.LayoutParams(-1, -2);
            financialLp.setMargins(0, dp(8), 0, dp(2));
            item.addView(financial, financialLp);

            LinearLayout actions = row();
            actions.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            if (!done) smallAction(actions, "✓ انجام شد", Color.rgb(36, 105, 93), () ->
                    new AlertDialog.Builder(this).setTitle("تأیید انجام سرویس")
                            .setMessage(recurrenceMonths > 0
                                    ? "سرویس «" + a + "» انجام شده است؟ سررسید بعدی به‌صورت خودکار ایجاد می‌شود."
                                    : "آیا سرویس «" + a + "» انجام شده است؟")
                            .setPositiveButton("بله، انجام شد", (d, w) -> {
                                String nextDate = db.markServiceDoneAndScheduleNext(id, todayJalali());
                                if (nextDate != null) toast("سرویس انجام شد؛ سررسید بعدی: " + nextDate);
                                else toast("سرویس انجام‌شده ثبت شد");
                                showServiceList(filter);
                            })
                            .setNegativeButton("انصراف", null).show());
            if (recurrenceMonths > 0 && recurrenceGroup > 0) {
                smallAction(actions, "تغییر مبلغ دوره‌ای", PANEL2, () -> changeRecurringAmount(recurrenceGroup, serviceCost, filter));
            } else if (!done) {
                smallAction(actions, "فعال‌کردن تکرار", PANEL2, () -> enableRecurrenceForExistingService(id, date, filter));
            }
            smallAction(actions, "حذف", Color.rgb(142, 55, 65), () -> confirmDelete(id, "services"));
            item.addView(actions);
            card(content, item);
        }
        c.close();
        addButton("＋ ثبت پروندهٔ ساختمان و سرویس", GOLD, () -> showForm("services"));
        addButton("بازگشت به خانه", PANEL2, () -> showHome());
    }

    private void metaCell(LinearLayout parent, String title, String value) {
        LinearLayout cell = column();
        cell.setPadding(dp(9), dp(8), dp(9), dp(8));
        cell.setBackground(shape(dark ? Color.rgb(20, 37, 53) : Color.rgb(246, 249, 253), 12,
                dark ? Color.rgb(34, 57, 75) : Color.rgb(218, 229, 239)));
        cell.addView(text(title, 9, muted(), false));
        TextView val = text(value, 11, txt(), true);
        val.setMaxLines(2);
        val.setEllipsize(android.text.TextUtils.TruncateAt.END);
        cell.addView(val);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, -2, 1);
        lp.setMargins(dp(3), 0, dp(3), 0);
        parent.addView(cell, lp);
    }

    private int jalaliDay(String date) {
        if (date == null) return 0;
        String[] parts = normalizeJalaliDate(date).split("/");
        if (parts.length != 3) return 0;
        try { return Integer.parseInt(parts[2]); } catch (Exception ignored) { return 0; }
    }

    private String recurrenceLabel(int months) {
        if (months == 1) return "ماهانه";
        if (months == 2) return "هر دو ماه";
        if (months == 3) return "هر سه ماه";
        return "تک‌سرویس";
    }

    private void enableRecurrenceForExistingService(int serviceId, String dueDate, String filter) {
        String[] options = {"هر ماه", "هر ۲ ماه", "هر ۳ ماه"};
        new AlertDialog.Builder(this)
                .setTitle("فعال‌کردن سرویس دوره‌ای")
                .setMessage("پس از انجام این سرویس، سررسید بعدی خودکار ساخته می‌شود.")
                .setItems(options, (dialog, which) -> {
                    int interval = which + 1;
                    boolean enabled = db.enableRecurrence(serviceId, interval, dueDate);
                    toast(enabled ? "تکرار " + recurrenceLabel(interval) + " فعال شد" : "این سرویس قابل تغییر نیست");
                    showServiceList(filter);
                })
                .setNegativeButton("انصراف", null)
                .show();
    }

    private void changeRecurringAmount(long groupId, long currentAmount, String filter) {
        long amountToShow = db.openRecurringAmount(groupId, currentAmount);
        EditText amount = edit("مبلغ جدید سرویس (تومان)", String.valueOf(amountToShow), InputType.TYPE_CLASS_NUMBER);
        LinearLayout wrap = column();
        wrap.setPadding(dp(16), dp(6), dp(16), 0);
        wrap.addView(text("این تغییر روی سرویس‌های انجام‌نشدهٔ همین برنامهٔ دوره‌ای اعمال می‌شود؛ هزینه‌های تعمیر قبلی تغییر نمی‌کنند.", 12, muted(), false));
        wrap.addView(amount);
        new AlertDialog.Builder(this)
                .setTitle("تغییر مبلغ سرویس‌های آینده")
                .setView(wrap)
                .setPositiveButton("ذخیرهٔ مبلغ", (dialog, which) -> {
                    long updatedAmount = parseAmount(amount.getText().toString());
                    int changed = db.updateRecurringAmount(groupId, updatedAmount);
                    toast(changed > 0 ? "مبلغ سرویس‌های باز به‌روزرسانی شد" : "سرویس بازی برای تغییر مبلغ پیدا نشد");
                    showServiceList(filter);
                })
                .setNegativeButton("انصراف", null)
                .show();
    }

    private void tab(LinearLayout parent, String label, boolean selected, Runnable action) {
        TextView t = text(label, 11, selected ? NAVY : txt(), true);
        t.setGravity(Gravity.CENTER);
        t.setPadding(dp(5), dp(10), dp(5), dp(10));
        t.setMinHeight(dp(40));
        GradientDrawable base = selected
                ? gradientShape(Color.rgb(247, 212, 132), GOLD, 13, Color.rgb(255, 231, 179))
                : gradientShape(dark ? Color.rgb(16, 35, 53) : Color.rgb(255, 255, 255),
                    dark ? Color.rgb(11, 27, 43) : Color.rgb(246, 249, 253), 13,
                    dark ? Color.rgb(39, 63, 83) : Color.rgb(211, 224, 237));
        t.setBackground(ripple(base));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, -2, 1);
        lp.setMargins(dp(3), dp(4), dp(3), dp(5));
        parent.addView(t, lp);
        t.setOnClickListener(v -> action.run());
    }

    private void smallAction(LinearLayout parent, String label, int color, Runnable action) {
        boolean danger = label.contains("حذف");
        boolean done = label.contains("انجام");
        int bgColor = danger ? (dark ? Color.rgb(61, 32, 40) : Color.rgb(255, 241, 240))
                : done ? (dark ? Color.rgb(19, 59, 53) : Color.rgb(234, 248, 242)) : panel();
        int stroke = danger ? Color.rgb(131, 65, 73) : done ? Color.rgb(45, 112, 93)
                : (dark ? Color.rgb(45, 71, 91) : Color.rgb(211, 224, 237));
        int textColor = danger ? Color.rgb(255, 185, 190) : done ? Color.rgb(167, 230, 207) : txt();
        TextView t = text(label, 10, textColor, true);
        t.setGravity(Gravity.CENTER);
        t.setMinHeight(dp(40));
        t.setMaxLines(2);
        t.setPadding(dp(5), dp(8), dp(5), dp(8));
        t.setBackground(ripple(shape(bgColor, 12, stroke)));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, -2, 1);
        lp.setMargins(dp(3), dp(8), dp(3), 0);
        parent.addView(t, lp);
        t.setOnClickListener(v -> action.run());
    }

        private String money(long amount) {
        java.text.NumberFormat f=java.text.NumberFormat.getInstance(new java.util.Locale("fa","IR"));
        return f.format(amount);
    }

    private String normalizeJalaliDate(String value) {
        String d=value.trim().replace('-', '/'); StringBuilder ascii=new StringBuilder();
        for(int i=0;i<d.length();i++){char ch=d.charAt(i); if(ch>='۰'&&ch<='۹') ascii.append((char)(ch-'۰'+'0')); else if(ch>='٠'&&ch<='٩') ascii.append((char)(ch-'٠'+'0')); else ascii.append(ch);}
        String[] parts=ascii.toString().split("/");
        if(parts.length!=3)return ascii.toString();
        try{return String.format(java.util.Locale.US,"%04d/%02d/%02d",Integer.parseInt(parts[0]),Integer.parseInt(parts[1]),Integer.parseInt(parts[2]));}catch(Exception e){return ascii.toString();}
    }

    private boolean validDate(String value) {
        if(value==null) return false;
        String d=value.trim().replace('-', '/').replace('٫','/');
        return d.matches("\\d{4}/\\d{1,2}/\\d{1,2}") || d.matches("[۰-۹]{4}/[۰-۹]{1,2}/[۰-۹]{1,2}");
    }

    private String addJalaliMonths(String inputDate, int monthsToAdd, int preferredDay) {
        String normalized = normalizeJalaliDate(inputDate);
        if (!validDate(normalized)) normalized = todayJalali();
        String[] parts = normalized.split("/");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int day = Integer.parseInt(parts[2]);
        if (preferredDay <= 0 || preferredDay > 31) preferredDay = day;
        long monthIndex = (long) year * 12L + (month - 1) + monthsToAdd;
        int targetYear = (int) (monthIndex / 12L);
        int targetMonth = (int) (monthIndex % 12L) + 1;
        int maxDay;
        if (targetMonth <= 6) maxDay = 31;
        else if (targetMonth <= 11) maxDay = 30;
        else maxDay = isJalaliLeapYear(targetYear) ? 30 : 29;
        int targetDay = Math.min(preferredDay, maxDay);
        return String.format(java.util.Locale.US, "%04d/%02d/%02d", targetYear, targetMonth, targetDay);
    }

    // The 33-year Persian-calendar cycle covers modern operational dates well.
    private boolean isJalaliLeapYear(int year) {
        int r = year % 33;
        return r == 1 || r == 5 || r == 9 || r == 13 || r == 17 || r == 22 || r == 26 || r == 30;
    }

    private String todayJalali() {
        java.util.Calendar cal=java.util.Calendar.getInstance();
        int gy=cal.get(java.util.Calendar.YEAR), gm=cal.get(java.util.Calendar.MONTH)+1, gd=cal.get(java.util.Calendar.DAY_OF_MONTH);
        int[] gdm={0,31,28,31,30,31,30,31,31,30,31,30,31};
        boolean leap=(gy%4==0 && gy%100!=0)||gy%400==0; if(leap) gdm[2]=29;
        int gy2=gy-1600, gm2=gm-1, gd2=gd-1;
        int g_day_no=365*gy2+(gy2+3)/4-(gy2+99)/100+(gy2+399)/400;
        for(int i=0;i<gm2;i++) g_day_no+=gdm[i+1]; g_day_no+=gd2;
        int j_day_no=g_day_no-79; int j_np=j_day_no/12053; j_day_no%=12053;
        int jy=979+33*j_np+4*(j_day_no/1461); j_day_no%=1461;
        if(j_day_no>=366){jy+=(j_day_no-1)/365;j_day_no=(j_day_no-1)%365;}
        int[] jdm={31,31,31,31,31,31,30,30,30,30,30,29}; int jm=0;
        while(jm<11 && j_day_no>=jdm[jm]) {j_day_no-=jdm[jm];jm++;}
        int jd=j_day_no+1;
        return String.format(java.util.Locale.US,"%04d/%02d/%02d",jy,jm+1,jd);
    }

private void showDebtForm() {
    page = "debts";
    setup("ثبت طلب جدید");
    TextView intro = text("طلب‌ها را جدا از هزینهٔ سرویس ثبت و پیگیری کن.", 13, muted(), false);
    content.addView(intro);
    LinearLayout form = column();
    form.addView(text("اطلاعات طلب", 15, GOLD, true));
    EditText who = edit("نام مشتری یا ساختمان", "", InputType.TYPE_CLASS_TEXT);
    EditText amount = edit("مبلغ طلب (تومان)", "", InputType.TYPE_CLASS_NUMBER);
    EditText note = edit("توضیحات / تاریخ (اختیاری)", "", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
    form.addView(who); form.addView(amount); form.addView(note);
    TextView submit = button("ثبت طلب  ✓", GOLD, () -> {
        String a = who.getText().toString().trim(), b = amount.getText().toString().trim(), c = note.getText().toString().trim();
        if (a.isEmpty() || b.isEmpty()) { toast("نام و مبلغ را وارد کنید"); return; }
        db.insert("debts", a, b, c); hideKeyboard(); toast("طلب ثبت شد"); showList("debts");
    });
    LinearLayout.LayoutParams blp = new LinearLayout.LayoutParams(-1, -2);
    blp.setMargins(0, dp(7), 0, 0); form.addView(submit, blp);
    card(content, form);
}

        private void confirmDelete(int id,String type) {
        new AlertDialog.Builder(this)
            .setTitle(userName+" عزیز")
            .setMessage("از حذف اطلاعات مطمئن هستید؟\nاین عملیات قابل بازگشت نیست.")
            .setPositiveButton("حذف", (d,w)->{db.delete(id,type);showList(type);})
            .setNegativeButton("انصراف",null)
            .show();
    }

private void showSettings() {
    page = "settings";
    setup("تنظیمات");
    LinearLayout account = column();
    account.addView(text("حساب کاربری", 15, GOLD, true));
    account.addView(text("نام کاربر: " + userName, 13, txt(), false));
    account.addView(text("شماره تماس: " + userPhone, 13, muted(), false));
    card(content, account);

    LinearLayout theme = column();
    theme.addView(text("حالت نمایش", 15, GOLD, true));
    theme.addView(text(dark ? "حالت فعلی: تیره" : "حالت فعلی: روشن", 12, muted(), false));
    card(content, theme);
    addButton(dark ? "فعال‌کردن حالت روشن" : "فعال‌کردن حالت تیره", PANEL2, () -> {
        dark = !dark;
        getSharedPreferences("profile", MODE_PRIVATE).edit().putBoolean("dark", dark).apply();
        showSettings();
    });

    LinearLayout fonts = column();
    fonts.addView(text("اندازهٔ نوشته‌ها", 15, GOLD, true));
    fonts.addView(text("اندازهٔ فعلی: " + (fontScale < 0.95 ? "کوچک" : fontScale > 1.05 ? "بزرگ" : "متوسط"), 12, muted(), false));
    card(content, fonts);
    LinearLayout fontActions = row();
    fontActions.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
    TextView small = button("کوچک", PANEL2, () -> setFont(0.9f));
    TextView medium = button("متوسط", PANEL2, () -> setFont(1.0f));
    TextView large = button("بزرگ", PANEL2, () -> setFont(1.15f));
    for (TextView t : new TextView[]{small, medium, large}) {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, -2, 1);
        p.setMargins(dp(3), 0, dp(3), 0); fontActions.addView(t, p);
    }
    content.addView(fontActions);

    LinearLayout support = column();
    support.addView(text("پشتیبانی سرویس‌یار گلدرانی", 15, txt(), true));
    support.addView(text("0912-468-2580", 15, GOLD, true));
    card(content, support);
    addButton("تماس با پشتیبانی", PANEL2, () -> dial("09124682580"));
    addButton("خروج از حساب کاربری", PANEL2, () -> new AlertDialog.Builder(this)
            .setTitle(userName + " عزیز")
            .setMessage("از حساب کاربری خارج می‌شوید؟")
            .setPositiveButton("خروج", (d, w) -> {
                getSharedPreferences("profile", MODE_PRIVATE).edit().remove("name").remove("phone").apply();
                userName = ""; userPhone = ""; showLogin();
            }).setNegativeButton("انصراف", null).show());
}

        private void setFont(float f) {fontScale=f;getSharedPreferences("profile",MODE_PRIVATE).edit().putFloat("font",f).apply();showSettings();}
    private void dial(String number) {try{startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+number)));}catch(Exception e){toast("امکان باز کردن شماره‌گیر وجود ندارد");}}
    private void toast(String s) {Toast.makeText(this,s,Toast.LENGTH_SHORT).show();}
    private void hideKeyboard() {try{((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(root.getWindowToken(),0);}catch(Exception ignored){}}

    private long parseAmount(String raw) { if(raw==null)return 0; StringBuilder digits=new StringBuilder(); for(int i=0;i<raw.length();i++){char ch=raw.charAt(i); if(ch>='۰'&&ch<='۹') digits.append((char)(ch-'۰'+'0')); else if(ch>='٠'&&ch<='٩') digits.append((char)(ch-'٠'+'0')); else if(ch>='0'&&ch<='9') digits.append(ch); } try{return Long.parseLong(digits.toString());}catch(Exception e){return 0;} }


    // Consistent vector-like icons drawn locally; no emoji fonts or external icon package required.
private class IconView extends View {
    private final android.graphics.Paint p = new android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG);
    private final String kind;
    private final int color;
    IconView(String label, int tint) { super(MainActivity.this); kind = label; color = tint; }
    @Override protected void onDraw(android.graphics.Canvas c) {
        super.onDraw(c);
        float x = getWidth() / 2f, y = getHeight() / 2f;
        p.setColor(color); p.setStrokeWidth(dp(2));
        p.setStrokeCap(android.graphics.Paint.Cap.ROUND); p.setStrokeJoin(android.graphics.Paint.Join.ROUND);
        p.setStyle(android.graphics.Paint.Style.STROKE);
        float l = x - dp(10), r = x + dp(10), t = y - dp(9), b = y + dp(10);
        if (kind.equals("home") || kind.equals("خانه")) {
            c.drawLine(x, y - dp(11), x - dp(10), y - dp(1), p);
            c.drawLine(x - dp(10), y - dp(1), x - dp(10), y + dp(10), p);
            c.drawLine(x - dp(10), y + dp(10), x + dp(10), y + dp(10), p);
            c.drawLine(x + dp(10), y + dp(10), x + dp(10), y - dp(1), p);
            c.drawLine(x + dp(10), y - dp(1), x, y - dp(11), p);
            c.drawRect(x - dp(3), y + dp(2), x + dp(3), y + dp(10), p);
        } else if (kind.equals("register") || kind.equals("ثبت")) {
            c.drawLine(x - dp(10), y, x + dp(10), y, p);
            c.drawLine(x, y - dp(10), x, y + dp(10), p);
        } else if (kind.equals("settings") || kind.contains("تنظیم")) {
            c.drawCircle(x, y, dp(6), p); c.drawCircle(x, y, dp(2), p);
            for (int a = 0; a < 8; a++) { double q = a * Math.PI / 4;
                c.drawLine(x + (float)Math.cos(q) * dp(8), y + (float)Math.sin(q) * dp(8),
                        x + (float)Math.cos(q) * dp(11), y + (float)Math.sin(q) * dp(11), p); }
        } else if (kind.equals("debts") || kind.contains("طلب")) {
            c.drawRoundRect(l, t, r, b, dp(2), dp(2), p);
            c.drawLine(l + dp(3), y - dp(3), r - dp(3), y - dp(3), p);
            c.drawLine(l + dp(3), y + dp(2), x + dp(2), y + dp(2), p);
            c.drawLine(r - dp(5), y + dp(5), r - dp(2), y + dp(5), p);
        } else if (kind.equals("files") || kind.contains("ساختمان") || kind.contains("پرونده")) {
            c.drawRoundRect(l, t, r, b, dp(2), dp(2), p);
            c.drawLine(l, y - dp(3), r, y - dp(3), p);
            c.drawLine(x, y - dp(3), x, b, p);
            c.drawLine(l + dp(3), y + dp(3), x - dp(2), y + dp(3), p);
            c.drawLine(x + dp(3), y + dp(3), r - dp(3), y + dp(3), p);
        } else if (kind.contains("امروز")) {
            c.drawCircle(x, y, dp(9), p); c.drawLine(x, y, x, y - dp(5), p); c.drawLine(x, y, x + dp(5), y + dp(3), p);
        } else if (kind.contains("انجام")) {
            c.drawCircle(x, y, dp(9), p); c.drawLine(x - dp(5), y, x - dp(1), y + dp(4), p); c.drawLine(x - dp(1), y + dp(4), x + dp(6), y - dp(5), p);
        } else {
            c.drawRoundRect(l, t, r, b, dp(3), dp(3), p);
            c.drawLine(x, t, x, b, p);
        }
    }
}

        private class Db extends SQLiteOpenHelper {
        Db(Context c){super(c,"goldarani_local.db",null,6);}
        @Override public void onCreate(SQLiteDatabase d) {
            d.execSQL("CREATE TABLE services (_id INTEGER PRIMARY KEY AUTOINCREMENT, a TEXT, b TEXT, c TEXT, completed INTEGER NOT NULL DEFAULT 0, service_amount INTEGER NOT NULL DEFAULT 0, repair_details TEXT, repair_amount INTEGER NOT NULL DEFAULT 0, address TEXT, manager TEXT, manager_phone TEXT, recurrence_months INTEGER NOT NULL DEFAULT 0, recurrence_group INTEGER NOT NULL DEFAULT 0, recurrence_day INTEGER NOT NULL DEFAULT 0)");
            d.execSQL("CREATE TABLE buildings (_id INTEGER PRIMARY KEY AUTOINCREMENT, a TEXT, b TEXT, c TEXT)");
            d.execSQL("CREATE TABLE debts (_id INTEGER PRIMARY KEY AUTOINCREMENT, a TEXT, b TEXT, c TEXT)");
        }
        @Override public void onUpgrade(SQLiteDatabase d,int oldV,int newV){
            if(oldV<2) d.execSQL("ALTER TABLE services ADD COLUMN completed INTEGER NOT NULL DEFAULT 0");
            if(oldV<3) {
                d.execSQL("ALTER TABLE services ADD COLUMN service_amount INTEGER NOT NULL DEFAULT 0");
                d.execSQL("ALTER TABLE services ADD COLUMN repair_details TEXT");
                d.execSQL("ALTER TABLE services ADD COLUMN repair_amount INTEGER NOT NULL DEFAULT 0");
            }
            if(oldV<4) {
                d.execSQL("ALTER TABLE services ADD COLUMN address TEXT");
                d.execSQL("ALTER TABLE services ADD COLUMN manager TEXT");
                d.execSQL("ALTER TABLE services ADD COLUMN manager_phone TEXT");
            }
            if(oldV<5) {
                d.execSQL("ALTER TABLE services ADD COLUMN recurrence_months INTEGER NOT NULL DEFAULT 0");
                d.execSQL("ALTER TABLE services ADD COLUMN recurrence_group INTEGER NOT NULL DEFAULT 0");
            }
            if(oldV<6) {
                d.execSQL("ALTER TABLE services ADD COLUMN recurrence_day INTEGER NOT NULL DEFAULT 0");
                d.execSQL("UPDATE services SET recurrence_day=CAST(substr(b,9,2) AS INTEGER) WHERE recurrence_months>0 AND recurrence_day=0 AND b IS NOT NULL AND length(b)>=10");
            }
        }
        void insert(String table,String a,String b,String c){getWritableDatabase().execSQL("INSERT INTO "+table+"(a,b,c) VALUES(?,?,?)",new Object[]{a,b,c});}
        long insertService(String a,String date,String details,long serviceAmount,String repairDetails,long repairAmount,String address,String manager,String phone,int recurrenceMonths){
            SQLiteDatabase w = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("a", a); values.put("b", date); values.put("c", details);
            values.put("completed", 0); values.put("service_amount", serviceAmount);
            values.put("repair_details", repairDetails); values.put("repair_amount", repairAmount);
            values.put("address", address); values.put("manager", manager); values.put("manager_phone", phone);
            values.put("recurrence_months", recurrenceMonths); values.put("recurrence_group", 0);
            values.put("recurrence_day", recurrenceMonths > 0 ? jalaliDay(date) : 0);
            long id = w.insertOrThrow("services", null, values);
            if (recurrenceMonths > 0) {
                ContentValues group = new ContentValues(); group.put("recurrence_group", id);
                w.update("services", group, "_id=?", new String[]{String.valueOf(id)});
            }
            return id;
        }
        void delete(int id,String table){getWritableDatabase().delete(table,"_id=?",new String[]{String.valueOf(id)});}
        Cursor list(String table){return getReadableDatabase().rawQuery("SELECT _id,a,b,c FROM "+table+" ORDER BY _id DESC",null);}
        Cursor serviceList(String filter,String today){
            String where="";
            if(filter.equals("today")) where=" WHERE completed=0 AND b=?";
            else if(filter.equals("overdue")) where=" WHERE completed=0 AND b<?";
            else if(filter.equals("pending")) where=" WHERE completed=0";
            else if(filter.equals("completed")) where=" WHERE completed=1";
            String sql="SELECT _id,a,b,c,completed,service_amount,repair_details,repair_amount,address,manager,manager_phone,recurrence_months,recurrence_group,recurrence_day FROM services"+where+" ORDER BY b ASC, _id DESC";
            return (filter.equals("today") || filter.equals("overdue"))
                    ? getReadableDatabase().rawQuery(sql,new String[]{today})
                    : getReadableDatabase().rawQuery(sql,null);
        }
        String markServiceDoneAndScheduleNext(int id, String today){
            SQLiteDatabase w = getWritableDatabase();
            String nextDate = null;
            w.beginTransaction();
            Cursor c = null;
            try {
                c = w.rawQuery("SELECT a,b,c,service_amount,address,manager,manager_phone,recurrence_months,recurrence_group,completed,recurrence_day FROM services WHERE _id=?", new String[]{String.valueOf(id)});
                if (!c.moveToFirst() || c.getInt(9) == 1) return null;
                String name=c.getString(0), dueDate=c.getString(1), details=c.getString(2);
                long amount=c.getLong(3);
                String address=c.getString(4), manager=c.getString(5), phone=c.getString(6);
                int interval=c.getInt(7); long groupId=c.getLong(8);
                int anchorDay = c.getInt(10);
                if (anchorDay <= 0) anchorDay = jalaliDay(dueDate);
                c.close(); c = null;
                ContentValues done = new ContentValues(); done.put("completed", 1);
                int changed = w.update("services", done, "_id=? AND completed=0", new String[]{String.valueOf(id)});
                if (changed == 1 && interval > 0) {
                    if (groupId <= 0) { groupId = id; ContentValues group = new ContentValues(); group.put("recurrence_group", groupId); w.update("services", group, "_id=?", new String[]{String.valueOf(id)}); }
                    nextDate = addJalaliMonths(dueDate, interval, anchorDay);
                    int guard = 0;
                    while (nextDate.compareTo(normalizeJalaliDate(today)) <= 0 && guard++ < 120) nextDate = addJalaliMonths(nextDate, interval, anchorDay);
                    ContentValues future = new ContentValues();
                    future.put("a", name); future.put("b", nextDate); future.put("c", details);
                    future.put("completed", 0); future.put("service_amount", amount);
                    future.put("repair_details", ""); future.put("repair_amount", 0);
                    future.put("address", address); future.put("manager", manager); future.put("manager_phone", phone);
                    future.put("recurrence_months", interval); future.put("recurrence_group", groupId);
                    future.put("recurrence_day", anchorDay);
                    w.insertOrThrow("services", null, future);
                }
                w.setTransactionSuccessful();
                return nextDate;
            } finally {
                if (c != null) c.close();
                w.endTransaction();
            }
        }
        boolean enableRecurrence(int serviceId, int interval, String dueDate){
            if (interval < 1 || interval > 3) return false;
            ContentValues values = new ContentValues();
            values.put("recurrence_months", interval);
            values.put("recurrence_group", serviceId);
            values.put("recurrence_day", jalaliDay(dueDate));
            return getWritableDatabase().update("services", values, "_id=? AND completed=0", new String[]{String.valueOf(serviceId)}) == 1;
        }
        long openRecurringAmount(long groupId, long fallback){
            Cursor c = getReadableDatabase().rawQuery("SELECT service_amount FROM services WHERE recurrence_group=? AND completed=0 ORDER BY b ASC, _id DESC LIMIT 1", new String[]{String.valueOf(groupId)});
            long amount = fallback;
            if (c.moveToFirst()) amount = c.getLong(0);
            c.close();
            return amount;
        }
        int updateRecurringAmount(long groupId, long amount){
            ContentValues values = new ContentValues(); values.put("service_amount", amount);
            return getWritableDatabase().update("services", values, "recurrence_group=? AND completed=0", new String[]{String.valueOf(groupId)});
        }
        int countTodayServices(String today){Cursor c=getReadableDatabase().rawQuery("SELECT COUNT(*) FROM services WHERE completed=0 AND b=?",new String[]{today});int n=0;if(c.moveToFirst())n=c.getInt(0);c.close();return n;}
        int countOverdueServices(String today){Cursor c=getReadableDatabase().rawQuery("SELECT COUNT(*) FROM services WHERE completed=0 AND b<?",new String[]{today});int n=0;if(c.moveToFirst())n=c.getInt(0);c.close();return n;}
        long totalDebts(){
            long total=0;
            Cursor c=getReadableDatabase().rawQuery("SELECT b FROM debts",null);
            while(c.moveToNext()) total+=parseAmount(c.getString(0));
            c.close();
            return total;
        }
        int count(String table){Cursor c=getReadableDatabase().rawQuery("SELECT COUNT(*) FROM "+table,null);int n=0;if(c.moveToFirst())n=c.getInt(0);c.close();return n;}
    }
}
