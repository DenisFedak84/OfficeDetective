package criminalintent.android.bignerdranch.com.criminalintent.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.view.Display;
import android.widget.ImageView;

public class PictureUtils {
    public static final String LOG_TAG = PictureUtils.class.getSimpleName();

    //Получение объекта BitmapDrawable по данным локального файла, масштабированного по текущим размерам окна
    public static BitmapDrawable getScaledDrawable(Activity activity, String path) {
        // получаем размер дисплея
        Display display = activity.getWindowManager().getDefaultDisplay();
        float destWidth = display.getWidth();
        float destHeight = display.getHeight();

        // получение размеров изображения
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        // масштабируем
        int inSampleSize = 1;
        if (srcHeight > destHeight || srcWidth > destWidth) {
            if (srcWidth > srcHeight) {
                inSampleSize = Math.round(srcHeight / destHeight);
            } else {
                inSampleSize = Math.round(srcWidth / destWidth);
            }
        }

        // сетим новый размер нашей bitmap
        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return new BitmapDrawable(activity.getResources(), bitmap);
    }

    public static void cleanImageView(ImageView imageView) {
        if (!(imageView.getDrawable() instanceof BitmapDrawable))
            return;
        // стирание изображения для экономии памяти
        BitmapDrawable b = (BitmapDrawable) imageView.getDrawable();
        b.getBitmap().recycle();
        imageView.setImageDrawable(null);
    }

    public static int getDegree(int orientation) {
        int degree = 0;
      //  try {
          //  ExifInterface ei = new ExifInterface(photoPath);
            //int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
         //   int orientation = context.getResources().getConfiguration().orientation;
          //  int orientation =
            //Log.d(LOG_TAG, "Orientation = " + orientation);

            switch (orientation) {
                case ExifInterface.ORIENTATION_NORMAL:
                    degree = 0;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                case ExifInterface.ORIENTATION_UNDEFINED:
                    degree = 0;
                    break;
                default:
                    degree = 90;
            }

       // } catch (IOException e) {
//            e.printStackTrace();
//        }
        return degree;
    }

    public static Bitmap rotateImageIfRequired(Bitmap img, int rotation) {
        // Detect rotation
        if (rotation != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
            img.recycle();

            return rotatedImg;
        } else {
            return img;
        }
    }

    public static Bitmap cropToSquare(Bitmap bitmap){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newWidth = (height > width)? width:height;
        int newHeight = (height>width)?height - (height-width):height;
        int cropW = (width-height)/2;
        cropW = (cropW<0)?0:cropW;
        int cropH = (height-width)/2;
        cropH = (cropH<0)?0:cropH;

        return Bitmap.createBitmap(bitmap, cropW, cropH, newWidth, newHeight);
    }
}
