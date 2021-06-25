/*
 *
 * @author Naomi Hindriks
 */

$(document).ready(() => {
    //Get dom elements
    const $backgroundColor = $("#background-color");
    const $mainContent = $(".content");
    const $backgroundImage = $("#background-image");
    const $header = $("#header");

    // Set height en min height of the background image
    $backgroundImage.css({
        "min-height": "calc(938px - " + $header.outerHeight() + "px)"
    });

    // Set height of the background color (to the same height as the main content)
    $backgroundColor.css({
        "height": $mainContent.outerHeight() + "px"
    });

    $(window).resize(() => {
        // Set the height of the background color (to the same height as the main content)
        $backgroundColor.css({
            "height": $mainContent.outerHeight() + "px"
        });
    });
});