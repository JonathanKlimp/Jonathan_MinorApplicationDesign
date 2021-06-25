/*
 *
 * @author Naomi Hindriks
 */
$(document).ready(() => {
    // Get dom elements
    const $wrapper = $(".wrapper");
    const $footer = $("footer");

    let footerHeight = $footer.outerHeight();

    $wrapper.css({
        "padding-bottom": "calc(" + footerHeight + "px)"
    });

});