$(document).ready(() => {
    const $mainSection = $("#main-wrapper");
    const $header = $("#header");

    $mainSection.css({
       "height": "calc(100% - " + $header.outerHeight() + "px)"
    });
});