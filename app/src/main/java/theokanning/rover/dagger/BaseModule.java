package theokanning.rover.dagger;

import android.content.Context;

import dagger.Module;

@Module
public class BaseModule {

    private Context context;

    public BaseModule(Context context) {
        this.context = context;
    }
}
