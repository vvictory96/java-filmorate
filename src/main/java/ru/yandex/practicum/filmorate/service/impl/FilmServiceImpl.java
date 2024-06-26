package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikesFilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;

import static java.lang.String.format;

@Slf4j
@Service
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikesFilmStorage likesFilmStorage;

    private final DirectorStorage directorStorage;

    @Autowired
    public FilmServiceImpl(@Qualifier("FilmDbStorage") FilmStorage filmStorage,
                           @Qualifier("LikesFilmDbStorage") LikesFilmStorage likesFilmStorage,
                           @Qualifier("UserDbStorage") UserStorage userStorage,
                           @Qualifier("DirectorDbStorage") DirectorStorage directorStorage
    ) {
        this.filmStorage = filmStorage;
        this.likesFilmStorage = likesFilmStorage;
        this.userStorage = userStorage;
        this.directorStorage = directorStorage;
    }


    @Override
    public Film createFilm(Film film) {
        log.info(format("Start create idFilm = [%s]", film.getId()));
        if (film.getReleaseDate().isBefore(LocalDate.parse("1895-12-28"))) {
            throw new ValidationException("Date is before 1895-12-28", HttpStatus.BAD_REQUEST.value());
        }
        return filmStorage.createFilm(film);
    }

    @Override
    public Film updateFilm(Film film) {
        log.info(format("Start update idFilm = [%s]", film.getId()));
        return filmStorage.updateFilm(film);
    }

    @Override
    public boolean deleteFilm(int idFilm) {
        log.info(format("Start delete idFilm = [%s]", idFilm));
        return filmStorage.deleteFilm(idFilm);
    }

    @Override
    public Film getFilm(int idFilm) {
        log.info(format("Start get idFilm = [%s]", idFilm));
        return filmStorage.getFilm(idFilm);
    }

    @Override
    public List<Film> getAllFilms() {
        log.info("Start get all films");
        return filmStorage.getAllFilms();
    }

    @Override
    public void addLike(int idFilm, int idUser) {
        log.info(format("Start add like idUser = [%s] to idFilm = [%s]", idUser, idFilm));
        likesFilmStorage.addLike(idFilm, idUser);
        log.info(format("Like added to idFilm = [%s]", idFilm));
    }

    @Override
    public void deleteLike(int idFilm, int idUser) {
        log.info(format("Start delete like idUser = [%s] from idFilm = [%s]", idUser, idFilm));
        Film film = filmStorage.getFilm(idFilm);
        User user = userStorage.getUser(idUser);
        likesFilmStorage.deleteLike(film.getId(), user.getId());
        log.info(format("Like was delete to idFilm = [%s]", idFilm));
    }

    /**
     * Возвращает список из первых count фильмов по количеству лайков.
     * Если значение параметра count не задано, верните первые 10.
     *
     * @param count   количество
     * @param genreId айди жанра
     * @param year    год
     * @return список фильмов
     */
    @Override
    public List<Film> getPopularFilms(int count, Integer genreId, Integer year) {
        log.info(format("Start get popular films count = [%s], genreId = [%s], year = [%s]", count, genreId, year));
        return likesFilmStorage.getPopularFilms(count, genreId, year);
    }

    /**
     * Возвращает список общих фильмов двух пользователей, отсортированных по популярности.
     *
     * @param idUser   пользователь
     * @param idFriend пользователь
     * @return список фильмов
     */
    @Override
    public List<Film> getCommonFilms(int idUser, int idFriend) {
        log.info(format("Start get common films idUser = [%s] and idFriend = [%s]", idUser, idFriend));
        return filmStorage.getCommonFilms(idUser, idFriend);
    }

    @Override
    public List<Film> searchFilmsByDirector(int idDirector) {
        log.info("Start get films by director, no sorted");
        return directorStorage.searchFilmsByDirector(idDirector);
    }

    @Override
    public List<Film> searchFilmsByDirectorSortedByYear(int idDirector) {
        log.info("Start get films by director, sorted by year");
        return directorStorage.searchFilmsByDirectorSortedByYear(idDirector);
    }

    @Override
    public List<Film> searchFilmsByDirectorSortedByLikes(int idDirector) {
        log.info("Start get films by director, sorted by likes");
        return directorStorage.searchFilmsByDirectorSortedByLikes(idDirector);
    }

    /**
     * Возвращает список фильмов, отсортированных по популярности.
     *
     * @param query запрос
     * @param by    параметры запроса
     * @return список фильмов
     */
    @Override
    public List<Film> searchFilms(String query, String by) {
        log.info(format("Start search films query = [%s], by = [%s]", query, by));
        System.out.println();
        return filmStorage.searchFilms(query, by);
    }
}