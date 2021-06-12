<%
    //(new Bula.Fetcher.Controller.Testing.GetFeed(new Bula.Fetcher.Context()).execute();

    Bula.Fetcher.Context $context = new Bula.Fetcher.Context(request, response);
    Bula.Fetcher.Controller.Testing.GetFeed $feed = new Bula.Fetcher.Controller.Testing.GetFeed($context);
    $feed.execute();
%>