import Header from "../components/header";
import HomeButtons from "../components/homeButtons";

const Home = () => {
  return (
    <>
      <Header isHomePage={true} />
      <div className="min-h-screen flex justify-center">
        <HomeButtons />
      </div>
    </>
  );
};

export default Home;
