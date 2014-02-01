
var similarpages = {};

similarpages.similarServiceBaseUrl = '/ubsearch/ubsearch';

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
sourcepage=location.pathname;
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
					for (var i = 0; i < data.similarPages.length; i++) {
						divElement.append( "<a  class=\"text_box_audio\" href=\"" + data.similarPages[i].url + "\">" + data.similarPages[i].title + "</a>" );
					}				
				}
          }
      });

}

$(document).ready(function () {



similarpages.initialize();

})

