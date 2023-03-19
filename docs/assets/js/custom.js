function scrollToDiv(div_name) {
    let div = document.getElementById(div_name);
    div.scrollIntoView({behavior: 'smooth'});
}
