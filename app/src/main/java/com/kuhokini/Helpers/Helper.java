package com.kuhokini.Helpers;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
//import com.hishd.tinycart.model.Cart;
//import com.hishd.tinycart.model.Item;
//import com.hishd.tinycart.util.TinyCartHelper;
import com.kuhokini.APIModels.VariantResponse;
import com.kuhokini.Account.Login;
import com.kuhokini.Activities.CheckOut;
import com.kuhokini.Activities.WebView;
import com.kuhokini.R;
import com.kuhokini.databinding.CustomDialogBinding;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Helper {

    public static String StartDate;
    public static String EndDate;
    public static String RoomID;
    public static String AdapterRoomId;
    public static Integer Adult;
    public static Integer Child;
    public static Integer Room;
    public static Integer Gap;
    public static Boolean Enable;
    public static int RoomAvailed;
    public static String Selected_Hotel;
    public static String Selected_Phone;
    public static String Selected_Email;
    public static String RoomNo;
    public static String dateWiseChange;
    public static int selectedPosition = -1;
    public static ArrayList<String> list = new ArrayList<>();

    public static void setWhiteStatusBarWithDarkIcons(Window window, int whiteColorResId) {
        window.setStatusBarColor(ContextCompat.getColor(window.getContext(), whiteColorResId));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = window.getDecorView();
            int flags = decor.getSystemUiVisibility();

            // Add LIGHT_STATUS_BAR flag
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            decor.setSystemUiVisibility(flags);
        }


        window.setNavigationBarColor(ContextCompat.getColor(window.getContext(), whiteColorResId));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            View decor = window.getDecorView();
            int flags = decor.getSystemUiVisibility();

            // Add light nav bar flag (dark icons)
            flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            decor.setSystemUiVisibility(flags);
        }
    }


    public static String capitalizeWords(String sentence) {
        if (sentence == null || sentence.trim().isEmpty()) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        String[] words = sentence.split("\\s+"); // Split by spaces

        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))  // Capitalize first letter
                        .append(word.substring(1).toLowerCase())        // Lowercase the rest
                        .append(" "); // Add space
            }
        }

        return result.toString().trim(); // Remove trailing space
    }

    public interface DateRangeListener {
        void onDateRangeSelected(String formattedStartDate, String formattedEndDate);
    }

    public static void saveData(Context context, String name, String data) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(name, data);
        editor.apply();
        editor.commit();
    }

    public static String getData(Context context, String name) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString(name, null);
    }

    public static void showDateRangePicker(FragmentManager fragmentManager,
                                           String startDateFormat,
                                           String endDateFormat,
                                           DateRangeListener listener) {

        MaterialDatePicker.Builder<androidx.core.util.Pair<Long, Long>> builder =
                MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Select Date Range");

        MaterialDatePicker<androidx.core.util.Pair<Long, Long>> picker = builder.build();

        picker.addOnPositiveButtonClickListener(selection -> {
            Long start = selection.first;
            Long end = selection.second;

            // Apply user-provided formats
            SimpleDateFormat sdfStart = new SimpleDateFormat(startDateFormat, Locale.getDefault());
            SimpleDateFormat sdfEnd = new SimpleDateFormat(endDateFormat, Locale.getDefault());

            String formattedStartDate = sdfStart.format(new Date(start));
            String formattedEndDate = sdfEnd.format(new Date(end));

            listener.onDateRangeSelected(formattedStartDate, formattedEndDate);
        });

        picker.show(fragmentManager, picker.toString());
    }

    public static String convertSQliToFormatDate(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(dateString, formatter);
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd LLL yyyy");
        return localDate.format(formatter2);
    }

    public static void changeId(String id, Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("Ids", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("id", id);
        editor.apply();
    }

    public static LocalDate convertSQliStToLocalD(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(dateString, formatter);
    }

    public static String longToOnlyTime(Long date) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm a");
        String dat = formatter.format(date);
        return dat;
    }

    public static void startCounter(int countTill, TextView textView, String prefix, String suffix){
        ValueAnimator animator = ValueAnimator.ofInt(0, countTill);
        animator.setDuration(3000); // Animation duration in milliseconds
        animator.addUpdateListener(valueAnimator -> {
            int animatedValue = (int) valueAnimator.getAnimatedValue();
            textView.setText(prefix + animatedValue + suffix);
        });
        animator.start();
    }

    public static void changeStatusBarColor(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getColor(color));
            View decor = window.getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    public static void changeStatusBarToDark(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getColor(color));
            View decor = window.getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }
    }

    public static int getDaysDifference(String date1, String date2) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date startDate = dateFormat.parse(date1);
            Date endDate = dateFormat.parse(date2);
            long diffInMillis = Math.abs(endDate.getTime() - startDate.getTime());
            return (int) TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static String formatDate(String inputTime, String inputFormat, String outputFormat) {
        try {
            // Parse and format date
            SimpleDateFormat inputFormatter = new SimpleDateFormat(inputFormat);
            SimpleDateFormat outputFormatter = new SimpleDateFormat(outputFormat);
            Date date = inputFormatter.parse(inputTime);
            return outputFormatter.format(date);
        } catch (Exception e) {
            // Return a default value in case of failure
            return inputTime;
        }
    }

    public static String dateKey(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
        return date.format(formatter);
    }

    public static int convertSqlDateKey(long calendarTimeMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return Integer.parseInt(dateFormat.format(new Date(calendarTimeMillis)));
    }

    public static int getArrayPosition(String[] array, String inputText) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equalsIgnoreCase(inputText)) { // Case-insensitive match
                return i;
            }
        }
        return 0; // Return 0 if no match is found
    }



    public static LocalDate convertLongToLocalDate(long milliseconds) {
        Instant instant = Instant.ofEpochMilli(milliseconds);
        return instant.atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static String longToDate(Long date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd LLL yyyy");
        String dat = formatter.format(date);
        return dat;
    }
    public static String longToDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd LLL yyyy");
        return date.format(formatter);
    }

    public static String longToMonthYear(Long date) {
        SimpleDateFormat formatter = new SimpleDateFormat("LLL yyyy");
        String dat = formatter.format(date);
        return dat;
    }

    public static LocalDate convertStToLocalD(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd LLL yyyy");
        return LocalDate.parse(dateString, formatter);
    }

    public static LocalDate convertSQliDateToLocalDate(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(dateString, formatter);
    }

    public static String getSharedId(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("Ids", Context.MODE_PRIVATE);
        String id = sharedPreferences.getString("id", "null");
        return id;
    }

    public static String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM, yyyy");
        return dateFormat.format(date);
    }

    public static String formatDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM, yyyy");
        return date.format(formatter);
    }
    public static String formatDate(Long date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM, yyyy");
        return dateFormat.format(date);
    }

    public static String formatDateSql(Long date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    public static String formatDateByMonth(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM, yyyy");
        return dateFormat.format(date);
    }

    public static boolean isChromeCustomTabsSupported(@NonNull final Context context) {
        Intent serviceIntent = new Intent("android.support.customtabs.action.CustomTabsService");
        serviceIntent.setPackage("com.android.chrome");
        List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentServices(serviceIntent, 0);
        return !resolveInfos.isEmpty();
    }
    public static void openChromeTab(String link, Activity activity){
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(ContextCompat.getColor(activity, R.color.purple));
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(activity, Uri.parse(link));
    }

    public static String extractVideoIdFromYouTubeLink(String youtubeLink) {
        String videoId = null;

        // Regular expression pattern for extracting video ID from YouTube link
        Pattern pattern = Pattern.compile("(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/shorts\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%2F|youtu.be%2F|%2Fv%2F|e%2F|watch%3Fv%3D|watch%3Ffeature=player_embedded&v%3D|%2Fshorts%2F)[^#\\&\\?\\n]*");
        Matcher matcher = pattern.matcher(youtubeLink);

        if (matcher.find()) {
            videoId = matcher.group();
        }

        return videoId;
    }

    public static void plusBtn(Context context, EditText textView){
        String no = textView.getText().toString();
        int number = Integer.parseInt(no);

        if (number < 10){
            int newNum = number + 1 ;
            textView.setText(String.valueOf(newNum));
        }else {
            Toast.makeText(context, "value exceed", Toast.LENGTH_LONG).show();
        }
    }

    public static void minusBtn(Context context, TextView textView){
        String no = textView.getText().toString();
        int number = Integer.parseInt(no);

        if (number > 1){
            int newNum = number - 1 ;
            textView.setText(String.valueOf(newNum));
        }else {
            Toast.makeText(context, "value exceed", Toast.LENGTH_LONG).show();
        }
    }

    public static void openLink(Activity activity, String link){
        if (Helper.isChromeCustomTabsSupported(activity)){
            Helper.openChromeTab(link, activity);
        }else {
            Intent i = new Intent(activity.getApplicationContext(), WebView.class);
            i.putExtra("share", link);
            activity.startActivity(i);
        }
    }

    public static void executeCall(Call<ApiResponse> call, Activity activity, ProgressDialog dialog) {
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                ApiResponse apiResponse = response.body();
                if (!apiResponse.getStatus().equalsIgnoreCase("success")){
                    Helper.showActionDialog(activity, apiResponse.getStatus(), apiResponse.getMessage(), "Okay",
                            null, false, new Helper.DialogButtonClickListener() {
                                @Override
                                public void onYesButtonClicked() {}
                                @Override
                                public void onNoButtonClicked() {}
                                @Override
                                public void onCloseButtonClicked() {}
                            });
                }else {
                    Toast.makeText(activity, apiResponse.getStatus(), Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Helper.showActionDialog(activity, "Failed", t.getLocalizedMessage(), "Okay",
                        null, false, new Helper.DialogButtonClickListener() {
                            @Override
                            public void onYesButtonClicked() {}
                            @Override
                            public void onNoButtonClicked() {}
                            @Override
                            public void onCloseButtonClicked() {}
                        });
            }
        });
    }

    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    public static void showOnlyMessage(Activity activity, String title, String content){
        Helper.showActionDialog(activity, capitalizeFirstLetter(title), content, "Okay", null, true,
                new DialogButtonClickListener() {
                    @Override
                    public void onYesButtonClicked() {}
                    @Override
                    public void onNoButtonClicked() {}
                    @Override
                    public void onCloseButtonClicked() {}
                });
    }

    public static String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    public static void showLoginDialog(Activity activity){
        Helper.showActionDialog(activity, "Login Required",
                "Please login to place order. Click below login button.",
                "Login", "Later", true, new Helper.DialogButtonClickListener() {
                    @Override
                    public void onYesButtonClicked() {
                        activity.startActivity(new Intent(activity.getApplicationContext(), Login.class));
                    }
                    @Override
                    public void onNoButtonClicked() {}
                    @Override
                    public void onCloseButtonClicked() {}
                });
    }

    public interface DialogButtonClickListener {
        void onYesButtonClicked();
        void onNoButtonClicked();
        void onCloseButtonClicked();
    }

    @SuppressLint("ResourceAsColor")
    public static void showActionDialog(
            Activity activity, String title,
            String content, String yesBtn, String noBtn, boolean closeBtn,
            DialogButtonClickListener listener) { // Add listener parameter
        CustomDialogBinding dialogBinding = CustomDialogBinding.inflate(LayoutInflater.from(activity));

        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setView(dialogBinding.getRoot())
                .create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        dialogBinding.titleText.setText(title);
        dialogBinding.messageText.setText(Html.fromHtml(content));
        if (yesBtn != null) {
            dialogBinding.loginBtn.setVisibility(View.VISIBLE);
            dialogBinding.yesBtnText.setText(yesBtn);
        }else {
            dialogBinding.loginBtn.setVisibility(View.GONE);
        }

        if (noBtn != null) {
            dialogBinding.noBtn.setVisibility(View.VISIBLE);
            dialogBinding.noBtnText.setText(noBtn);
        }else {
            dialogBinding.noBtn.setVisibility(View.GONE);
        }

        if (closeBtn){
            dialogBinding.closeBtn.setVisibility(View.VISIBLE);
        }else {
            dialogBinding.closeBtn.setVisibility(View.GONE);
        }

        dialogBinding.noBtn.setOnClickListener(v -> {
            if (listener != null) {
                listener.onNoButtonClicked();
            }
            dialog.dismiss();
        });

        dialogBinding.loginBtn.setOnClickListener(v -> {
            if (listener != null) {
                listener.onYesButtonClicked();
            }
            dialog.dismiss();
        });

        dialogBinding.closeBtn.setOnClickListener(v->{
            if (listener != null) {
                listener.onCloseButtonClicked();
            }
            dialog.dismiss();
        });

        dialog.show();
    }

}
