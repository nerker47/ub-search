
var similarpages = {};

similarpages.similarServiceBaseUrl = 'http://localhost:8080/ubsearch/ubsearch';

similarpages.initialize = function()
{
$( ".similarpages" ).each(function( index ) {
similarpages.showSimilarPages($( this ));
});
};

similarpages.showSimilarPages = function(divElement)
{
var sourcepage = divElement.data('forpage');
if (sourcepage==undefined)
{
sourcepage=location.href;
}
//alert(sourcepage);

similarpages.getSimilarPages(sourcepage, divElement);
};


similarpages.getSimilarPages = function(sourceUrl, divElement)
{

      $.ajax({ // ajax call starts
          url: similarpages.similarServiceBaseUrl, // JQuery loads serverside.php
          data: 'similar=' + sourceUrl, // Send value of the clicked button
          dataType: 'json', // Choosing a JSON datatype
          success: function(data) // Variable data contains the data we get from serverside
          {
//				alert(data.similarPages);
				if (data.similarPages)
				{
					divElement.append( "<p>similar pages for " + sourceUrl + "</p>" );
					for (var i = 0; i < data.similarPages.length; i++) {
						divElement.append( "<p><a href=\"" + data.similarPages[i].url + "\">" + data.similarPages[i].title + "</a></p>" );
					}				
				}
          }
      });

}

$(document).ready(function () {



similarpages.initialize();

})

