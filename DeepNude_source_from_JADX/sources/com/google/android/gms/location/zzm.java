package com.google.android.gms.location;

import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.internal.zzde;
import com.google.android.gms.internal.zzchh;
import com.google.android.gms.tasks.TaskCompletionSource;

final class zzm extends zzde<zzchh, LocationAvailability> {
    zzm(FusedLocationProviderClient fusedLocationProviderClient) {
    }

    protected final /* synthetic */ void zza(zzb zzb, TaskCompletionSource taskCompletionSource) {
        taskCompletionSource.setResult(((zzchh) zzb).zzaxb());
    }
}
