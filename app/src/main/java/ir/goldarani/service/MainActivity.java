package ir.goldarani.service;

import android.app.*;
import android.os.Bundle;
import android.content.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.text.InputType;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MainActivity extends Activity {
    private final int NAVY = Color.rgb(5,17,32);
    private final int PANEL = Color.rgb(11,31,51);
    private final int PANEL2 = Color.rgb(17,42,66);
    private final int GOLD = Color.rgb(235,181,73);
    private final int WHITE = Color.rgb(242,246,251);
    private final int MUTED = Color.rgb(153,174,196);
    private final int BLUE = Color.rgb(41,72,101);
    private final int TEAL = Color.rgb(36,105,93);
    private final int ORANGE = Color.rgb(178,119,41);
    private final int PURPLE = Color.rgb(88,102,124);
    private Db db;
    private String userName = "", userPhone = "";
    private boolean dark = true;
    private float fontScale = 1.0f;
    private LinearLayout root, content;
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
        d.setColor(color); d.setCornerRadius(radius * getResources().getDisplayMetrics().density);
        if (stroke != 0) d.setStroke((int)(1 * getResources().getDisplayMetrics().density), stroke);
        return d;
    }

    private TextView text(String s, int size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(s); t.setTextColor(color); t.setTextSize(size * fontScale);
        t.setTextDirection(View.TEXT_DIRECTION_RTL);
        t.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        t.setTypeface(iranSans != null ? Typeface.create(iranSans, bold ? Typeface.BOLD : Typeface.NORMAL)
                                       : Typeface.create("sans-serif", bold ? Typeface.BOLD : Typeface.NORMAL));
        t.setPadding(dp(2), dp(3), dp(2), dp(3));
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
    root = column();
    root.setBackgroundColor(bg());
    root.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);

    LinearLayout bar = row();
    bar.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
    bar.setPadding(dp(16), dp(10), dp(16), dp(10));
    GradientDrawable barBg = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
            new int[]{dark ? Color.rgb(9, 27, 46) : Color.rgb(248, 251, 255),
                      dark ? Color.rgb(5, 17, 31) : Color.rgb(235, 242, 249)});
    barBg.setStroke(dp(1), dark ? Color.rgb(34, 62, 87) : Color.rgb(211, 224, 237));
    barBg.setCornerRadius(dp(18));
    bar.setBackground(barBg);

    TextView back = text("‹", 28, GOLD, true);
    back.setGravity(Gravity.CENTER);
    back.setBackground(shape(dark ? Color.rgb(17, 39, 60) : Color.WHITE, 15,
            dark ? Color.rgb(43, 73, 100) : Color.rgb(208, 222, 235)));
    bar.addView(back, new LinearLayout.LayoutParams(dp(42), dp(42)));
    back.setOnClickListener(v -> showHome());

    LinearLayout titleStack = column();
    titleStack.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
    titleStack.setPadding(dp(12), 0, dp(8), 0);
    TextView en = text("GOLDARANI  /  SERVICE", 9, GOLD, true);
    en.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
    titleStack.addView(en);
    TextView fa = text(title, 17, txt(), true);
    fa.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
    titleStack.addView(fa);
    bar.addView(titleStack, new LinearLayout.LayoutParams(0, -2, 1));

    TextView mark = text("G", 24, NAVY, true);
    mark.setGravity(Gravity.CENTER);
    mark.setBackground(shape(GOLD, 15, Color.rgb(255, 226, 163)));
    bar.addView(mark, new LinearLayout.LayoutParams(dp(44), dp(44)));

    LinearLayout.LayoutParams barLp = new LinearLayout.LayoutParams(-1, -2);
    barLp.setMargins(dp(10), dp(7), dp(10), dp(7));
    root.addView(bar, barLp);
    View line = new View(this);
    line.setBackgroundColor(dark ? Color.rgb(43, 76, 102) : Color.rgb(215, 225, 236));
    root.addView(line, new LinearLayout.LayoutParams(-1, dp(1)));

    ScrollView sc = new ScrollView(this);
    sc.setFillViewport(false);
    sc.setClipToPadding(false);
    content = column();
    content.setPadding(dp(16), dp(14), dp(16), dp(20));
    sc.addView(content);
    root.addView(sc, new LinearLayout.LayoutParams(-1, 0, 1));

    if (!"ورود به سرویس‌یار".equals(title)) addBottomNav();
    setContentView(root);
}

    private TextView button(String label, int color, Runnable action) {
    boolean primary = color == GOLD;
    TextView b = text(label, 14, primary ? NAVY : txt(), true);
    b.setGravity(Gravity.CENTER);
    b.setMinHeight(dp(48));
    b.setPadding(dp(16), dp(12), dp(16), dp(12));
    b.setBackground(shape(primary ? GOLD : panel(), 14,
            primary ? Color.rgb(255, 224, 147) : (dark ? Color.rgb(43, 73, 98) : Color.rgb(210, 223, 235))));
    b.setOnClickListener(v -> action.run());
    return b;
}

    private void addButton(String label, int color, Runnable action) {
    TextView b = button(label, color, action);
    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
    lp.setMargins(0, dp(5), 0, dp(5));
    content.addView(b, lp);
}

    private void card(LinearLayout parent, View child) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(0, dp(5), 0, dp(7));
        child.setPadding(dp(16), dp(14), dp(16), dp(14));
        // Keep custom gradient backgrounds (for example, the dashboard hero).
        if (child.getBackground() == null) {
            child.setBackground(shape(panel(), 18,
                    dark ? Color.rgb(33, 63, 88) : Color.rgb(211, 224, 237)));
        }
        parent.addView(child, lp);
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
    e.setPadding(dp(14), dp(11), dp(14), dp(11));
    e.setBackground(shape(dark ? Color.rgb(8, 25, 42) : Color.rgb(249, 251, 254), 13,
            dark ? Color.rgb(42, 70, 95) : Color.rgb(199, 214, 228)));
    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
    lp.setMargins(0, dp(5), 0, dp(7));
    e.setLayoutParams(lp);
    return e;
}

    private void showLogin() {
    page = "login";
    setup("ورود به سرویس‌یار");
    LinearLayout hero = column();
    hero.setGravity(Gravity.CENTER);
    hero.setPadding(dp(12), dp(22), dp(12), dp(20));
    TextView icon = text("G", 42, NAVY, true);
    icon.setGravity(Gravity.CENTER);
    icon.setBackground(shape(GOLD, 28, Color.rgb(255, 224, 147)));
    LinearLayout.LayoutParams ip = new LinearLayout.LayoutParams(dp(96), dp(96));
    ip.gravity = Gravity.CENTER;
    hero.addView(icon, ip);
    TextView title = text("سرویس‌یار گلدرانی", 24, GOLD, true);
    title.setGravity(Gravity.CENTER);
    title.setPadding(0, dp(12), 0, dp(2));
    hero.addView(title);
    TextView sub = text("مدیریت سرویس آسانسور، ساده و منظم", 14, muted(), false);
    sub.setGravity(Gravity.CENTER);
    hero.addView(sub);
    card(content, hero);

    LinearLayout form = column();
    form.addView(text("ورود به حساب کاربری", 16, txt(), true));
    form.addView(text("برای شروع، نام و شماره تماس را وارد کن.", 12, muted(), false));
    EditText name = edit("نام شما", "", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
    EditText phone = edit("شماره تماس", "", InputType.TYPE_CLASS_PHONE);
    form.addView(name);
    form.addView(phone);
    TextView submit = button("ورود به برنامه  ›", GOLD, () -> {
        String n = name.getText().toString().trim(), p = phone.getText().toString().trim();
        if (n.isEmpty() || p.isEmpty()) { toast("نام و شماره تماس را وارد کنید"); return; }
        userName = n; userPhone = p;
        getSharedPreferences("profile", MODE_PRIVATE).edit().putString("name", n).putString("phone", p).apply();
        hideKeyboard(); showHome();
    });
    LinearLayout.LayoutParams blp = new LinearLayout.LayoutParams(-1, -2);
    blp.setMargins(0, dp(7), 0, dp(3));
    form.addView(submit, blp);
    card(content, form);
    TextView foot = text("اطلاعات شما فقط روی همین دستگاه ذخیره می‌شود", 11, muted(), false);
    foot.setGravity(Gravity.CENTER);
    content.addView(foot);
}

    private void showHome() {
    page = "home";
    setup("خانه");

    LinearLayout welcome = column();
    welcome.setPadding(dp(17), dp(16), dp(17), dp(16));
    GradientDrawable heroBg = new GradientDrawable(GradientDrawable.Orientation.TL_BR,
            new int[]{dark ? Color.rgb(15, 39, 62) : Color.rgb(255, 255, 255),
                      dark ? Color.rgb(8, 24, 42) : Color.rgb(239, 246, 252)});
    heroBg.setCornerRadius(dp(22));
    heroBg.setStroke(dp(1), dark ? Color.rgb(44, 79, 107) : Color.rgb(210, 225, 238));
    welcome.setBackground(heroBg);

    LinearLayout brandRow = row();
    brandRow.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
    TextView logo = text("G", 27, NAVY, true);
    logo.setGravity(Gravity.CENTER);
    logo.setBackground(shape(GOLD, 16, Color.rgb(255, 224, 151)));
    brandRow.addView(logo, new LinearLayout.LayoutParams(dp(54), dp(54)));
    LinearLayout brandText = column();
    brandText.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
    brandText.setPadding(dp(12), 0, 0, 0);
    brandText.addView(text("سرویس‌یار گلدرانی", 20, GOLD, true));
    brandText.addView(text("مدیریت هوشمند سرویس آسانسور", 12, muted(), false));
    brandRow.addView(brandText, new LinearLayout.LayoutParams(0, -2, 1));
    welcome.addView(brandRow);
    View sep = new View(this);
    sep.setBackgroundColor(dark ? Color.rgb(43, 73, 98) : Color.rgb(218, 228, 238));
    LinearLayout.LayoutParams sepLp = new LinearLayout.LayoutParams(-1, dp(1));
    sepLp.setMargins(0, dp(12), 0, dp(10));
    welcome.addView(sep, sepLp);
    welcome.addView(text("سلام " + userName + " عزیز  👋", 16, txt(), true));
    welcome.addView(text("کارهای امروزت را مرتب و سریع مدیریت کن.", 12, muted(), false));
    card(content, welcome);

    addSectionHeading("دسترسی سریع");
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

    addSectionHeading("خلاصه وضعیت");
    LinearLayout stats = row(); stats.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
    stat(stats, "پرونده‌ها", db.count("services"), GOLD);
    stat(stats, "طلب‌های ثبت‌شده", db.count("debts"), GOLD);
    content.addView(stats);

    int overdueCount = db.countOverdueServices(todayJalali());
    LinearLayout overdueCard = column();
    overdueCard.setPadding(dp(14), dp(12), dp(14), dp(12));
    overdueCard.setBackground(shape(panel(), 16, overdueCount > 0 ? Color.rgb(130, 61, 66) : BLUE));
    overdueCard.addView(text("سرویس‌های عقب‌افتاده", 13, txt(), true));
    overdueCard.addView(text(String.valueOf(overdueCount) + " سرویس نیازمند پیگیری", 17,
            overdueCount > 0 ? Color.rgb(255, 150, 145) : GOLD, true));
    card(content, overdueCard);
    overdueCard.setOnClickListener(v -> showServiceList("overdue"));

    LinearLayout due = column();
    due.setPadding(dp(15), dp(13), dp(15), dp(13));
    due.setBackground(shape(panel(), 17, dark ? Color.rgb(39, 70, 96) : Color.rgb(211, 224, 237)));
    due.addView(text("مجموع طلب‌های ثبت‌شده", 13, muted(), true));
    due.addView(text(money(db.totalDebts()) + " تومان", 22, GOLD, true));
    due.addView(text("طلب‌ها به‌صورت جدا از هزینهٔ سرویس ثبت می‌شوند.", 11, muted(), false));
    LinearLayout.LayoutParams dueLp = new LinearLayout.LayoutParams(-1, -2);
    dueLp.setMargins(0, dp(8), 0, dp(7)); content.addView(due, dueLp);

    LinearLayout support = column();
    support.addView(text("پشتیبانی سرویس‌یار", 13, txt(), true));
    support.addView(text("0912-468-2580", 14, GOLD, true));
    card(content, support);
    addButton("تماس با پشتیبانی", PANEL2, () -> dial("09124682580"));
}

private void addSectionHeading(String title) {
    LinearLayout wrap = row();
    wrap.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
    View accent = new View(this);
    accent.setBackground(shape(GOLD, 3, 0));
    wrap.addView(accent, new LinearLayout.LayoutParams(dp(3), dp(22)));
    TextView label = text(title, 16, txt(), true);
    LinearLayout.LayoutParams tlp = new LinearLayout.LayoutParams(0, -2, 1);
    tlp.setMargins(dp(9), 0, 0, 0);
    wrap.addView(label, tlp);
    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
    lp.setMargins(0, dp(7), 0, dp(8));
    content.addView(wrap, lp);
}

    private void gridTile(LinearLayout parent, String emoji, String label, int color, Runnable click) {
    LinearLayout tile = column();
    tile.setGravity(Gravity.CENTER);
    tile.setPadding(dp(8), dp(9), dp(8), dp(9));
    tile.setBackground(shape(dark ? Color.rgb(11, 31, 51) : Color.WHITE, 17,
            dark ? Color.rgb(35, 66, 92) : Color.rgb(209, 223, 236)));
    IconView ic = new IconView(label, GOLD);
    LinearLayout.LayoutParams iconLp = new LinearLayout.LayoutParams(dp(27), dp(27));
    iconLp.bottomMargin = dp(8);
    tile.addView(ic, iconLp);
    TextView tx = text(label, 12, txt(), true);
    tx.setGravity(Gravity.CENTER);
    tile.addView(tx);
    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, dp(88), 1);
    lp.setMargins(dp(4), dp(4), dp(4), dp(4));
    parent.addView(tile, lp);
    tile.setOnClickListener(v -> click.run());
}

    private void stat(LinearLayout parent, String label, int count, int color) {
    LinearLayout box = column();
    box.setGravity(Gravity.CENTER);
    box.setPadding(dp(9), dp(10), dp(9), dp(10));
    box.setBackground(shape(dark ? Color.rgb(11, 31, 51) : Color.WHITE, 15,
            dark ? Color.rgb(35, 66, 92) : Color.rgb(209, 223, 236)));
    TextView n = text(String.valueOf(count), 21, GOLD, true);
    n.setGravity(Gravity.CENTER);
    box.addView(n);
    TextView l = text(label, 11, txt(), false);
    l.setGravity(Gravity.CENTER);
    box.addView(l);
    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, -2, 1);
    lp.setMargins(dp(4), 0, dp(4), 0);
    parent.addView(box, lp);
}

    private void addBottomNav() {
    LinearLayout nav = row();
    nav.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
    nav.setPadding(dp(6), dp(6), dp(6), dp(6));
    nav.setBackground(shape(dark ? Color.rgb(7, 22, 38) : Color.rgb(250, 252, 255), 20,
            dark ? Color.rgb(36, 66, 91) : Color.rgb(208, 221, 234)));
    navItem(nav, "home", "خانه", page.equals("home"), () -> showHome());
    navItem(nav, "files", "پرونده‌ها", page.equals("services") || page.equals("buildings"), () -> showServiceList("all"));
    navItem(nav, "register", "ثبت", page.equals("register"), () -> showForm("services"));
    navItem(nav, "debts", "طلب‌ها", page.equals("debts"), () -> showList("debts"));
    navItem(nav, "settings", "تنظیمات", page.equals("settings"), () -> showSettings());
    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
    lp.setMargins(dp(10), dp(4), dp(10), dp(7));
    root.addView(nav, lp);
}

    private void navItem(LinearLayout nav, String icon, String label, boolean selected, Runnable action) {
    LinearLayout item = column();
    item.setGravity(Gravity.CENTER);
    item.setPadding(dp(3), dp(5), dp(3), dp(5));
    item.setBackground(shape(selected ? (dark ? Color.rgb(20, 45, 68) : Color.rgb(239, 246, 252)) : Color.TRANSPARENT,
            13, selected && dark ? Color.rgb(45, 76, 100) : Color.TRANSPARENT));
    IconView mark = new IconView(icon, selected ? GOLD : muted());
    item.addView(mark, new LinearLayout.LayoutParams(dp(23), dp(23)));
    TextView title = text(label, 10, selected ? GOLD : muted(), selected);
    title.setGravity(Gravity.CENTER);
    item.addView(title);
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
    LinearLayout tabArea = column();
    LinearLayout tabsTop = row();
    tabsTop.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
    tab(tabsTop, "سررسید امروز", filter.equals("today"), () -> showServiceList("today"));
    tab(tabsTop, "عقب‌افتاده", filter.equals("overdue"), () -> showServiceList("overdue"));
    tab(tabsTop, "سرویس‌های باز", filter.equals("pending"), () -> showServiceList("pending"));
    tabArea.addView(tabsTop);
    LinearLayout tabsBottom = row();
    tabsBottom.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
    tab(tabsBottom, "انجام‌شده", filter.equals("completed"), () -> showServiceList("completed"));
    tab(tabsBottom, "همهٔ سرویس‌ها", filter.equals("all"), () -> showServiceList("all"));
    tabArea.addView(tabsBottom);
    content.addView(tabArea);

    LinearLayout summary = column();
    summary.addView(text("امروز  ·  " + todayJalali(), 13, GOLD, true));
    summary.addView(text("سرویس‌های باز امروز: " + db.countTodayServices(todayJalali()), 12, muted(), false));
    card(content, summary);

    Cursor c = db.serviceList(filter, todayJalali());
    if (c.getCount() == 0) {
        LinearLayout empty = column(); empty.setGravity(Gravity.CENTER); empty.setPadding(dp(18), dp(28), dp(18), dp(28));
        TextView e = text("—", 28, GOLD, true); e.setGravity(Gravity.CENTER); empty.addView(e);
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
        int statusBg = done ? Color.rgb(17, 64, 58) : overdue ? Color.rgb(83, 37, 44) : dueToday ? Color.rgb(65, 51, 28) : panel();
        LinearLayout item = column();
        LinearLayout titleRow = row();
        titleRow.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        TextView badge = text(statusLabel, 10, statusText, true);
        badge.setGravity(Gravity.CENTER);
        badge.setPadding(dp(9), dp(5), dp(9), dp(5));
        badge.setBackground(shape(statusBg, 10, 0));
        titleRow.addView(badge);
        TextView nm = text(a, 16, txt(), true);
        titleRow.addView(nm, new LinearLayout.LayoutParams(0, -2, 1));
        item.addView(titleRow);
        View divider = new View(this); divider.setBackgroundColor(dark ? Color.rgb(36, 63, 85) : Color.rgb(222, 231, 239));
        LinearLayout.LayoutParams dv = new LinearLayout.LayoutParams(-1, dp(1)); dv.setMargins(0, dp(8), 0, dp(7)); item.addView(divider, dv);
        item.addView(text("مدیر ساختمان: " + (manager == null || manager.isEmpty() ? "—" : manager), 12, txt(), false));
        item.addView(text("شماره تماس: " + (phone == null || phone.isEmpty() ? "—" : phone), 12, muted(), false));
        item.addView(text("آدرس: " + (address == null || address.isEmpty() ? "—" : address), 12, muted(), false));
        item.addView(text("تاریخ سررسید: " + (date == null ? "—" : date), 12, GOLD, true));
        if (recurrenceMonths > 0) {
            item.addView(text("سرویس دوره‌ای: " + recurrenceLabel(recurrenceMonths) + " · مبلغ برای دوره‌های بعد حفظ می‌شود", 11, muted(), false));
        }
        if (details != null && !details.isEmpty()) item.addView(text("توضیحات: " + details, 12, muted(), false));
        item.addView(text("هزینهٔ سرویس: " + money(serviceCost) + " تومان", 12, txt(), false));
        if (repairDetails != null && !repairDetails.isEmpty()) item.addView(text("شرح خرابی: " + repairDetails, 12, muted(), false));
        if (repairCost > 0) item.addView(text("هزینهٔ تعمیرات: " + money(repairCost) + " تومان", 12, txt(), false));
        item.addView(text("جمع کل: " + money(serviceCost + repairCost) + " تومان", 15, GOLD, true));
        LinearLayout actions = row();
        actions.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        if (!done) smallAction(actions, "✓  انجام شد", Color.rgb(36, 105, 93), () ->
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
    addButton("+ ثبت پرونده ساختمان و سرویس", GOLD, () -> showForm("services"));
    addButton("بازگشت به خانه", PANEL2, () -> showHome());
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
    t.setPadding(dp(4), dp(10), dp(4), dp(10));
    t.setBackground(shape(selected ? GOLD : panel(), 12,
            selected ? GOLD : (dark ? Color.rgb(40, 69, 94) : Color.rgb(209, 222, 234))));
    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, -2, 1);
    lp.setMargins(dp(2), dp(5), dp(2), dp(7));
    parent.addView(t, lp);
    t.setOnClickListener(v -> action.run());
}

    private void smallAction(LinearLayout parent, String label, int color, Runnable action) {
    boolean danger = label.contains("حذف");
    boolean done = label.contains("انجام");
    int bgColor = danger ? Color.rgb(83, 37, 44) : done ? Color.rgb(20, 66, 58) : panel();
    int stroke = danger ? Color.rgb(132, 63, 72) : done ? Color.rgb(45, 112, 93) : Color.rgb(45, 74, 99);
    int textColor = danger ? Color.rgb(255, 185, 190) : done ? Color.rgb(167, 230, 207) : txt();
    TextView t = text(label, 11, textColor, true);
    t.setGravity(Gravity.CENTER);
    t.setPadding(dp(11), dp(9), dp(11), dp(9));
    t.setBackground(shape(bgColor, 11, stroke));
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
