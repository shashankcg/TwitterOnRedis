$(document)
    .ready(
        function() {
            $
                .ajax({
                    url: "http://localhost:8080/feed/0/99",
                    method: "GET",
                    headers: {
                        "Accept": "application/json; odata=verbose"
                    },
                    dataType: 'json',
                    success: function(data) {
                        if (data.length > 0) {
                            $('#div1')
                                .append(
                                    '<h5> Tweet feed: </h5>');
                            $('#div1').append('<br>');
                            for (var i = 0; i < data.length; i++) {
                                $('#div1').append('<p class="solid">&nbsp;' + data[i].userName + '<br>' +
                                    '&nbsp;&nbsp;&nbsp;' + data[i].tweetContent + '<br></p><br>');
                            }
                        } else {
                            $('#div1').append('<h5> No Tweets from following</h5>');
                        }
                    },
                    error: function(data) {
                        alert("Error retrieving feeds " +
                            data);
                    }
                });

            $
                .ajax({
                    url: "http://localhost:8080/getAllUsers",
                    method: "GET",
                    headers: {
                        "Accept": "application/json; odata=verbose"
                    },
                    success: function(data) {
                        if (data.length > 0) {
                            for (var i = 0; i < data.length; i++) {
                                $('#div2').append(
                                    '<li>' + data[i] +
                                    '</li>');
                            }
                        } else {
                            $('#div2')
                                .append(
                                    '<li> No users in DB </li>');
                        }
                    },
                    error: function(data) {
                        alert("Error retrieving list of users " +
                            data);
                    }
                });

            $
                .ajax({
                    url: "http://localhost:8080/getAllFollowers",
                    method: "GET",
                    headers: {
                        "Accept": "application/json; odata=verbose"
                    },
                    success: function(data) {
                        if (data.length > 0) {
                            $('#followingUsersList')
                                .append(
                                    '<h5>You have ' +
                                    data.length +
                                    ' follower(s):</h5>');
                            for (var i = 0; i < data.length; i++) {
                                $('#followingUsersList')
                                    .append('<h6>' + data[i] + '</h6>');
                            }
                        } else {
                            $('#followingUsersList')
                                .append(
                                    '<h5> You do not have any followers yet </h5>');
                        }
                    },
                    error: function(data) {
                        alert("Error retrieving list of users " +
                            data);
                    }
                });

            $("#tweetButton").click(function() {
                $.ajax({
                    type: 'POST',
                    url: 'http://localhost:8080/tweet',
                    data: $("#tweetBox").val(),
                    contentType: "text/plain; charset=utf-8",
                    traditional: true,
                    success: function(data) {
                        alert("Posted tweet successfully!");
                    },
                    error: function(data) {
                        alert("Error posting tweet");
                    }
                });
            });

            $("#followUser").click(function() {
                $.ajax({
                    type: 'POST',
                    url: 'http://localhost:8080/follow',
                    data: $("#userNameToFollow").val(),
                    contentType: "text/plain; charset=utf-8",
                    traditional: true,
                    success: function(data) {
                        alert("User follow successful!");
                    },
                    error: function(data) {
                        alert("Error following user");
                    }
                });
            });

        });