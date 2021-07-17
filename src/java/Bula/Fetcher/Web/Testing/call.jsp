<%
    Bula.Fetcher.Context $context = new Bula.Fetcher.Context(request, response);
    Bula.Fetcher.Controller.Testing.CallMethod $call = new Bula.Fetcher.Controller.Testing.CallMethod($context);
    $call.execute();
    $context.close();
%>