package ${package}.home;

import ${package}.BaseWicketUnitTest;
import fiftyfive.wicket.test.WicketTestUtils;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.web.context.support.StaticWebApplicationContext;

public class HomePageTest extends BaseWicketUnitTest
{
    // @Mock MyService myService;
    
    @Override
    protected void initSpringContext(StaticWebApplicationContext ctx)
    {
        super.initSpringContext(ctx);
        
        // If HomePage had @SpringBean dependencies, you would mock them
        // (see @Mock example above), and register them with Spring here.
        // ctx.getBeanFactory().registerSingleton("myService", myService);
    }
    
    @Test
    public void testRender() throws Exception
    {
        this.tester.startPage(HomePage.class);
        this.tester.assertRenderedPage(HomePage.class);
        WicketTestUtils.assertValidMarkup(this.tester);
    }
}
