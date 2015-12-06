package theokanning.rover.dagger;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = BaseModule.class)
public interface BaseComponent {
    //application
    void inject(Application application);
}

