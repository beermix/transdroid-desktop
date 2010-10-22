package android.os;

import android.os.Parcel;

/**
 * Non-functional dummy of an Android Parcable
 */
public interface Parcelable {

	public abstract class Creator<T> {
		public abstract T createFromParcel(Parcel in);

		public abstract T[] newArray(int size);
	}

	void writeToParcel(Parcel dest, int flags);

	int describeContents();

}
