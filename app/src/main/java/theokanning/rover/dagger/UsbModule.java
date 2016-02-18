package theokanning.rover.dagger;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import theokanning.rover.usb.UsbScanner;

@Module
public class UsbModule {

    private Context context;

    public UsbModule(Context context) {
        this.context = context;
    }

    @Singleton
    @Provides
    UsbScanner provideUsbScanner(){
        return new UsbScanner(context);
    }
}
