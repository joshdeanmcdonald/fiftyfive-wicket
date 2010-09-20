/**
 * Copyright 2010 55 Minutes (http://www.55minutes.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fiftyfive.wicket.js;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fiftyfive.util.Assert;
import fiftyfive.wicket.js.locator.DefaultJavaScriptDependencyLocator;
import fiftyfive.wicket.js.locator.JavaScriptDependencyLocator;
import fiftyfive.wicket.js.locator.SearchLocation;
import org.apache.wicket.Application;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.resources.CompressedResourceReference;
import org.apache.wicket.markup.html.resources.JavascriptResourceReference;
import org.apache.wicket.util.time.Duration;

/**
 * Settings that affect how JavaScript dependencies are located. To obtain an 
 * instance, use the static {@link #get get()} method.
 * <p>
 * Most applications using out-of-the-box JavaScript widgets will be served
 * well by the default settings. However there are three important scenarios
 * to consider:
 * <ol>
 * <li><b>If your application already has a mechanism for including jQuery
 *     and/or jQuery UI in the &lt;head&gt;</b> (e.g. via your base page),
 *     then you should take care that fiftyfive-wicket-js is not including its
 *     own copies of these libraries. In this secnario, call
 *     {@link #setJQueryResource setJQueryResource()} and
 *     {@link #setJQueryUIResource setJQueryUIResource()} with references to
 *     the files that your application is already using (or {@code null},
 *     if you don't want fiftyfive-wicket-js to manage these resources at all).
 *     </li>
 * <li><b>If you have your own library of custom JavaScript files</b>,
 *     consider placing those files inside your classpath, and then use
 *     {@link #addLibraryPath addLibraryPath()} to make fiftyfive-wicket-js
 *     aware of them. Then you can use the angle-bracket dependency syntax
 *     or the single argument constructor for
 *     {@link JavaScriptDependency JavaScriptDependency}, and those files will
 *     be found automatically.</li>
 * <li><b>If you'd like to change the jQuery UI CSS theme</b>, call
 *     {@link #setJQueryUICSSResource setJQueryUICSSResource()} with a
 *     reference to the desired CSS file, or {@code null} if you don't want
 *     fiftyfive-wicket-js to manage this for you.</li>
 * </ol>
 */
public class JavaScriptDependencySettings
{
    private static final MetaDataKey<JavaScriptDependencySettings> SETTINGS_KEY
        = new MetaDataKey<JavaScriptDependencySettings>() {};
    
    private Application _app;
    private List<SearchLocation> _locations;
    private ResourceReference _jQueryResource;
    private ResourceReference _jQueryUIResource;
    private ResourceReference _jQueryUICSSResource;
    private Duration _traversalCacheDuration;
    private JavaScriptDependencyLocator _locator;
    
    /**
     * Returns the JavaScriptDependencySettings associated with the current
     * Wicket Application, creating a new default settings object if one does
     * not yet exist. This method can only be called within a Wicket thread.
     */
    public static JavaScriptDependencySettings get()
    {
        Application app = Application.get();
        if(null == app)
        {
            throw new IllegalStateException(
                "No thread-local Wicket Application object was found. " +
                "JavaScriptDependencySettings.get() can only be called " +
                "within a Wicket request."
            );
        }
        JavaScriptDependencySettings settings = app.getMetaData(SETTINGS_KEY);
        if(null == settings)
        {
            settings = new JavaScriptDependencySettings(app);
            app.setMetaData(SETTINGS_KEY, settings);
        }
        return settings;
    }
    
    protected JavaScriptDependencySettings(Application app)
    {
        super();
        _app = app;
        _locator = new DefaultJavaScriptDependencyLocator();

        _locations = new ArrayList<SearchLocation>();

        Class<?> c = JavaScriptDependencySettings.class;
        addLibraryPath(c, "");
        addLibraryPath(c, "lib/cookies");
        addLibraryPath(c, "lib/fiftyfive-utils");
        addLibraryPath(c, "lib/jquery-scrollto-1.4.2");
        addLibraryPath(c, "lib/strftime");
        
        _jQueryResource = new JavascriptResourceReference(
            JavaScriptDependencySettings.class,
            "lib/jquery-1.4.2/jquery-1.4.2.noconflict.min.js"
        );
        _jQueryUIResource = new JavascriptResourceReference(
            JavaScriptDependencySettings.class,
            "lib/jquery-ui-1.8.5/jquery-ui.min.js"
        );
        _jQueryUICSSResource = new CompressedResourceReference(
            JavaScriptDependencySettings.class,
            "lib/jquery-ui-1.8.5/themes/redmond/jquery-ui-1.8.5.redmond.css"
        );
    }
    
    public JavaScriptDependencyLocator getLocator()
    {
        return _locator;
    }
    
    public JavaScriptDependencySettings setLocator(JavaScriptDependencyLocator loc)
    {
        Assert.notNull(loc);
        this._locator = loc;
        return this;
    }
    
    public List<SearchLocation> getLibraryPaths()
    {
        return Collections.unmodifiableList(_locations);
    }

    public JavaScriptDependencySettings addLibraryPath(Class<?> cls, String path)
    {
        _locations.add(0, new SearchLocation(cls, path));
        return this;
    }
    
    public ResourceReference getJQueryResource()
    {
        return _jQueryResource;
    }

    public JavaScriptDependencySettings setJQueryResource(ResourceReference r)
    {
        this._jQueryResource = r;
        return this;
    }
    
    public ResourceReference getJQueryUIResource()
    {
        return _jQueryUIResource;
    }

    public JavaScriptDependencySettings setJQueryUIResource(ResourceReference r)
    {
        this._jQueryUIResource = r;
        return this;
    }
    
    public ResourceReference getJQueryUICSSResource()
    {
        return _jQueryUICSSResource;
    }

    /**
     * @return {@code this} to allow chaining
     */
    public JavaScriptDependencySettings setJQueryUICSSResource(ResourceReference r)
    {
        this._jQueryUICSSResource = r;
        return this;
    }
    
    /**
     * Returns the duration that JavaScript dependency traversal results
     * (i.e. the tree of dependencies as determined by parsing sprockets
     * directives) are allowed to be stored in cache.
     */
    public Duration getTraversalCacheDuration()
    {
        // Interpret null based on application mode
        if(null == _traversalCacheDuration)
        {
            if(Application.DEPLOYMENT.equals(_app.getConfigurationType()))
            {
                // Cache indefinitely
                return Duration.MAXIMUM;
            }
            // Disable cache
            return Duration.NONE;
        }
        return _traversalCacheDuration;
    }

    /**
     * Sets the duration that JavaScript dependency traversal results
     * (i.e. the tree of dependencies as determined by parsing sprockets
     * directives) are allowed to be stored in cache.
     * <p>
     * If this is set to {@code null} (the default), at runtime this will be
     * interpreted as a value of zero (cache disabled) if the application is
     * in development mode, and will be interpreted as the maximum duration
     * (effectively indefinite) if the application is in deployment mode.
     * 
     * @return {@code this} to allow chaining
     */
    public JavaScriptDependencySettings setTraversalCacheDuration(Duration d)
    {
        this._traversalCacheDuration = d;
        return this;
    }
}