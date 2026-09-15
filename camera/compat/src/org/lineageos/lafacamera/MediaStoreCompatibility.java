// SPDX-License-Identifier: Apache-2.0
package org.lineageos.lafacamera;

import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;

/** Remove OEM gallery annotations only when writing to AOSP MediaProvider. */
public final class MediaStoreCompatibility {
    private MediaStoreCompatibility() {}

    private static ContentValues values(Uri uri, ContentValues original) {
        if (uri == null || original == null || !"media".equals(uri.getAuthority())) {
            return original;
        }
        ContentValues result = new ContentValues(original);
        result.remove("_camera_quick_uri");
        result.remove("tagflags");
        result.remove("ext_tag_flags");
        return result;
    }

    public static Uri insert(ContentResolver resolver, Uri uri, ContentValues values) {
        return resolver.insert(uri, values(uri, values));
    }

    public static Uri insert(ContentProviderClient client, Uri uri, ContentValues values)
            throws RemoteException {
        return client.insert(uri, values(uri, values));
    }

    public static int update(ContentResolver resolver, Uri uri, ContentValues values,
            String selection, String[] args) {
        return resolver.update(uri, values(uri, values), selection, args);
    }

    public static int update(ContentProviderClient client, Uri uri, ContentValues values,
            String selection, String[] args) throws RemoteException {
        return client.update(uri, values(uri, values), selection, args);
    }

    public static int update(ContentResolver resolver, Uri uri, ContentValues values,
            Bundle extras) {
        return resolver.update(uri, values(uri, values), extras);
    }

    public static int update(ContentProviderClient client, Uri uri, ContentValues values,
            Bundle extras) throws RemoteException {
        return client.update(uri, values(uri, values), extras);
    }
}
