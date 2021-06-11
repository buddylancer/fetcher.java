<% 
    //(new Bula.Fetcher.Controller.Index(new Bula.Fetcher.Context(request, response)).execute();

    Bula.Fetcher.Context $context = new Bula.Fetcher.Context(request, response);
    Bula.Fetcher.Controller.Index $index = new Bula.Fetcher.Controller.Index($context);
    $index.execute();
%>