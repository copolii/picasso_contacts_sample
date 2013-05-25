package com.example.picasso_test;

import java.util.Locale;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MainActivity extends FragmentActivity implements
		LoaderCallbacks<Cursor> {

	private static final int LOADER_ID = 666;
	private ContactsAdapter mAdapter;
	private int mIconSize;

	private static final String[] PROJECTION = { ContactsContract.Contacts._ID,
			ContactsContract.Contacts.LOOKUP_KEY,
			ContactsContract.Contacts.DISPLAY_NAME, };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mIconSize = getResources().getDimensionPixelSize(R.dimen.icon_size);

		mAdapter = new ContactsAdapter();
		final ListView list = (ListView) findViewById(android.R.id.list);
		list.setAdapter(mAdapter);

		getSupportLoaderManager().initLoader(LOADER_ID, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return new CursorLoader(MainActivity.this,
				ContactsContract.Contacts.CONTENT_URI, PROJECTION, null, null,
				null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		if (null == arg1)
			return;

		mAdapter.swapCursor(arg1);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		mAdapter.swapCursor(null);
	}

	private class ContactsAdapter extends CursorAdapter {
		private final LayoutInflater mInflater;

		ContactsAdapter() {
			super(MainActivity.this, null, false);
			mInflater = LayoutInflater.from(MainActivity.this);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			final RowTag tag = (RowTag) view.getTag();

			final String id = cursor.getString(0);
			final String name = cursor.getString(2);
			tag.id.setText(id);
			tag.text.setText(name);

			final Uri lookupUri = ContactsContract.Contacts.lookupContact(
					getContentResolver(), Uri.withAppendedPath(
							ContactsContract.Contacts.CONTENT_LOOKUP_URI,
							cursor.getString(1)));

			if (null == lookupUri) return;

			Log.d("PicassoContacts", String.format(Locale.ENGLISH, "Uri '%s' for contact _id=%s (%s)", lookupUri, id, name));
			
			Picasso.with(context).load(lookupUri).placeholder(R.drawable.ic_default)
					.resize(mIconSize, mIconSize).centerCrop().into(tag.icon);
		}

		@Override
		public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
			final View v = mInflater.inflate(R.layout.list_item, null);
			v.setTag(new RowTag(v));
			return v;
		}
	}

	private static class RowTag {
		final ImageView icon;
		final TextView text;
		final TextView id;

		RowTag(final View v) {
			icon = (ImageView) v.findViewById(android.R.id.icon);
			text = (TextView) v.findViewById(android.R.id.title);
			id = (TextView) v.findViewById(android.R.id.text1);
		}
	}
}
