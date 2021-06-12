<% 
    //(new Bula.Fetcher.Controller.Rss(new Bula.Fetcher.Context()).execute(); 

    Bula.Fetcher.Context $context = new Bula.Fetcher.Context(request, response);
    Bula.Fetcher.Controller.Rss $rss = new Bula.Fetcher.Controller.Rss($context);
    $rss.execute();
%>