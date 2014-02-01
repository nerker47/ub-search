var similarpages = {};

similarpages.similarServiceBaseUrl = 'http://192.168.0.34/ubsearch/ubsearch';


similarpages.showSimilarPages = function(divElement)
{
var sourcepage = divElement.data('forpage');
if (sourcepage==undefined)
{
sourcepage=location.pathname;
}
//alert(sourcepage);

similarpages.getSimilarPages(sourcepage, divElement);
};






rendersimilarlinks = function(data)
        {
        $( ".similarpages" ).each(function( index ) {
        if (data.similarPages)
        {
            for (var i = 0; i < data.similarPages.length; i++)
                {
                $( this ).append( "<a  class=\"text_box_audio\" href=\"" + data.similarPages[i].url + "\">" + data.similarPages[i].title + "</a>" );
                }
        }
        });
        }


$(document).ready(function () {

 var x = $( ".similarpages" ).first();
 
 if (x!=undefined)
        {
	 var sourcepage = x.data('forpage');
	 if (sourcepage==undefined)
	 {
	 sourcepage=location.pathname;
	 }	 

        $.ajax({
            type: 'GET',
            url: 'http://similar.untergrund-blättle.ch/ubsearch/ubsearch?similar=/archiv/ausgabe2/jugendkriminalitaet.html&callback=rendersimilarlinks&this=mine',
            dataType: 'jsonp'
        });

        }
});
