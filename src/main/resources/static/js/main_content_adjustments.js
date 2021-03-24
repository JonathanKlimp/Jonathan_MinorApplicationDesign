$(document).ready(() => {
    //Get dom elements
    const $header = $("#header");
    const $homeMainContentRow = $("#home-main-content-row");
    const $homeMainContentCard = $("#home-main-content-row .card");
    const $homeMainContentCardBody = $("#home-main-content-row .card-body");
    const $homeMainContentCardContent = $("#home-main-content-row .card-content");
    const $homeMainContentCardNav = $("#home-main-content-row .card ul.nav");

    calculateHeightsHomeMainContent()

    $(window).resize(() => {
        calculateHeightsHomeMainContent()
    });

    function calculateHeightsHomeMainContent() {

        // Set min height of the homeMainContentRow
        $homeMainContentRow.css({
            "min-height": "calc(938px - " + $header.outerHeight() + "px)"
        });

        // Set min height of the homeMainContentCard to 80% of the height of homeMainContentRow
        $homeMainContentCard.css({
            "min-height": "calc(" + ($homeMainContentRow.height() * 0.8) + "px)"
        });

        // Set height of homeMainContentCardContent
        $homeMainContentCardContent.css({
            "height": "calc(" + ($homeMainContentCardBody.height() - $homeMainContentCardNav.outerHeight()) + "px)"
        });
    }

});